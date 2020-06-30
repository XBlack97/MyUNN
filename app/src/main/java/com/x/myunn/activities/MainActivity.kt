package com.x.myunn.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import com.x.myunn.firebase.FirebaseRepo

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

}
