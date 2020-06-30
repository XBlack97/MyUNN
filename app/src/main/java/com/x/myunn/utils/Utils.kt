package com.x.myunn.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.x.myunn.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min


fun glideLoad(c: Context, imageUri: String, imageView: ImageView, isPostImages: Boolean) {

    if (!isPostImages){
        Glide.with(c)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.profile)
            ).into(imageView)
    }else{

        Glide.with(c)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.profile)
            ).into(imageView)
    }


    }

fun mySnackBar(view: View, text: String) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(ContextCompat.getColor(view.context, R.color.colorPrimaryDark))
        val snackbarView = snackbar.view
        val tv: TextView = snackbarView.findViewById(R.id.snackbar_text)
        tv.setTextColor(Color.BLACK)
        snackbarView.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.light_gray
            )
        )
        snackbar.duration = TimeUnit.SECONDS.toMillis(2).toInt()
        snackbar.show()


    }

@Throws(IOException::class)
fun createImageFile(activity: Activity): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir      /* directory */
    )

    // Save a file: path for use with ACTION_VIEW intents
    //imageUri = image.absolutePath
    return image
}

@Throws(IOException::class)
fun createCacheImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "compressed_" + timeStamp + "_"
    val storageDir = File("/storage/emulated/0/Android/data/com.x.myunn/cache")
    if (!storageDir.exists()){
        storageDir.mkdir()
    }
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir      /* directory */
    )

    // Save a file: path for use with ACTION_VIEW intents
    //imageUri = image.absolutePath
    return image
}

fun getBitmapDrawablefromUri(activity: Activity, uri: Uri?): RoundedBitmapDrawable?{
    var getImage: Bitmap? = null
    var roundedBitmapDrawable: RoundedBitmapDrawable? = null
    try {
        if (Build.VERSION.SDK_INT < 28) {

            // Use the MediaStore to load the image.
            getImage = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)

            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), getImage)

            roundedBitmapDrawable.setCornerRadius(Math.max(getImage.getWidth(), getImage.getHeight()) / 8.0f)

        } else {

            val source = ImageDecoder.createSource(activity.contentResolver, uri!!)
            getImage = ImageDecoder.decodeBitmap(source)

            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), getImage)

            roundedBitmapDrawable.setCornerRadius(Math.max(getImage.getWidth(), getImage.getHeight()) / 8.0f)
        }

        return roundedBitmapDrawable
    }
    catch (e: Exception){
        e.printStackTrace()
    }


    return roundedBitmapDrawable
}

fun _getRealPathFromURI(contentURI: Uri, context: Context): String? {
    //val contentUri = Uri.parse(contentURI)
    val cursor = context.contentResolver.query(contentURI, null, null, null, null)
    return if (cursor == null) {
        contentURI.path
    } else {
        var res: String? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor.close()
        res
    }
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
