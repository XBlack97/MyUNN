package com.x.myunn.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.x.myunn.R
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = MainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

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

    fun glideLoad(c: Context, imageUri: String, imageView: CircleImageView){

        Glide.with(c)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.profile)
            ).into(imageView)
    }
}
