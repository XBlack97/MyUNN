package com.x.myunn.activities

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.x.myunn.R
import com.x.myunn.firebase.FirebaseRepo
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = MainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val menu = navView.menu
        val menuItem = menu.findItem(R.id.nav_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            menuItem?.iconTintList = null
            menuItem?.iconTintMode = null
        }

        Glide.with(this)
            .asBitmap()
            .load(FirebaseRepo().currentUserImageUrl)
            .apply(RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.profile)
            )
            .into(object : CustomTarget<Bitmap>(24, 24) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    menuItem?.icon = BitmapDrawable(resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        navView.setupWithNavController(navController)


        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id) {
                R.id.postDetailFragment -> navView.visibility = View.GONE
                R.id.nav_profile -> navView.visibility = View.VISIBLE
                R.id.nav_profilesetting -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }
    }

    fun glideLoad(c: Context, imageUri: String, imageView: ImageView) {

        Glide.with(c)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.profile)
            ).into(imageView)
    }

    class BottomNavigationBehavior<V : View>(context: Context, attrs: AttributeSet) :
        CoordinatorLayout.Behavior<V>(context, attrs) {

        @ViewCompat.NestedScrollType
        private var lastStartedType: Int = 0
        private var offsetAnimator: ValueAnimator? = null
        var isSnappingEnabled = false

        override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
        ) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
            child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
        }


        override fun onDependentViewRemoved(parent: CoordinatorLayout, child: V, dependency: View) {
            super.onDependentViewRemoved(parent, child, dependency)
            child.translationY = 0f
        }

        override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: V,
            dependency: View
        ): Boolean {
            return updateButton(child, dependency)
        }

        private fun updateButton(child: View, dependency: View): Boolean {
            if (dependency is Snackbar.SnackbarLayout) {
                val oldTranslation = child.translationY
                val height = dependency.height.toFloat()
                val newTranslation = dependency.translationY - height
                child.translationY = newTranslation

                return oldTranslation != newTranslation
            }
            return false
        }


        override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
            if (dependency is Snackbar.SnackbarLayout) {
                updateSnackbar(child, dependency)
            }
            return super.layoutDependsOn(parent, child, dependency)
        }

        private fun updateSnackbar(child: View, snackbarLayout: Snackbar.SnackbarLayout) {
            if (snackbarLayout.layoutParams is CoordinatorLayout.LayoutParams) {
                val params = snackbarLayout.layoutParams as CoordinatorLayout.LayoutParams

                params.anchorId = child.id
                params.anchorGravity = Gravity.TOP
                params.gravity = Gravity.TOP
                snackbarLayout.layoutParams = params
            }
        }

        override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int
        ): Boolean {
            if (axes != ViewCompat.SCROLL_AXIS_VERTICAL)
                return false

            lastStartedType = type
            offsetAnimator?.cancel()

            return true
        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
            if (!isSnappingEnabled)
                return

            // add snap behaviour
            // Logic here borrowed from AppBarLayout onStopNestedScroll code
            if (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH) {
                // find nearest seam
                val currTranslation = child.translationY
                val childHalfHeight = child.height * 0.5f

                // translate down
                if (currTranslation >= childHalfHeight) {
                    animateBarVisibility(child, isVisible = false)
                }
                // translate up
                else {
                    animateBarVisibility(child, isVisible = true)
                }
            }
        }

        private fun animateBarVisibility(child: View, isVisible: Boolean) {
            if (offsetAnimator == null) {
                offsetAnimator = ValueAnimator().apply {
                    interpolator = DecelerateInterpolator()
                    duration = 150L
                }

                offsetAnimator?.addUpdateListener {
                    child.translationY = it.animatedValue as Float
                }
            } else {
                offsetAnimator?.cancel()
            }

            val targetTranslation = if (isVisible) 0f else child.height.toFloat()
            offsetAnimator?.setFloatValues(child.translationY, targetTranslation)
            offsetAnimator?.start()
        }
    }

}
