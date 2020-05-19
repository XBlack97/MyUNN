package com.x.myunn.ui.profileSetting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.x.myunn.R
import com.x.myunn.activities.LogInActivity
import com.x.myunn.firebase.FirebaseRepo
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.fragment_profile_setting.view.*

class ProfileSettingFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileSettingFragment()
    }

    private lateinit var profileSettingViewModel: ProfileSettingViewModel
    val firebaseRepo = FirebaseRepo()

    val mAuth = FirebaseAuth.getInstance()

    private var checker = ""
    private lateinit var imageUri: Uri



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile_setting, container, false)

//        val bnv = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
//        //bnv.visibility = View.GONE
//
//        val navController = Navigation.findNavController(
//            requireActivity(),
//            R.id.nav_host_fragment
//        )// this maybe change
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            if(destination.id == R.id.nav_profilesetting) {
//                bnv.visibility = View.GONE
//            } else {
//                bnv.visibility = View.VISIBLE
//            }
//        }

        view.logout_btn.setOnClickListener {
            mAuth.signOut()

            val intent = Intent(context, LogInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        view.save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                updateImageUserInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        view.close_profile_btn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        view.change_image_text_btn.setOnClickListener {

            checker = "clicked"

            CropImage.activity().setAspectRatio(1, 1)
                .start(requireContext(), this)
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ProfileViewSettingModelFactory()
        profileSettingViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProfileSettingViewModel::class.java)

        profileSettingViewModel.loadUserInfo(
            mAuth.currentUser!!.uid, profile_image_view_setting, full_name_profile_setting,
            username_profile_setting, bio_profile_setting, requireContext()
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri

            profile_image_view_setting.setImageURI(imageUri)

        } else {
            println("img is null here")
        }
    }

    private fun updateImageUserInfo() {

        when {
            full_name_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Full Name is required...",
                Toast.LENGTH_LONG
            ).show()
            username_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Username is required...",
                Toast.LENGTH_LONG
            ).show()
            bio_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Bio is empty...",
                Toast.LENGTH_LONG
            ).show()
            imageUri == null -> Toast.makeText(context, "No image selected...", Toast.LENGTH_LONG)
                .show()

            else -> {
                val fullname = full_name_profile_setting.text.toString()
                val username = username_profile_setting.text.toString()
                val bio = bio_profile_setting.text.toString()

                profileSettingViewModel.updateImageUserInfo(
                    fullname,
                    username,
                    bio,
                    imageUri,
                    this@ProfileSettingFragment.requireContext()
                )

            }
        }
    }

    private fun updateUserInfoOnly() {

        when {
            full_name_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Full Name is required...",
                Toast.LENGTH_LONG
            ).show()
            username_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Username is required...",
                Toast.LENGTH_LONG
            ).show()
            bio_profile_setting.text.toString() == "" -> Toast.makeText(
                context,
                "Bio is empty...",
                Toast.LENGTH_LONG
            ).show()

            else -> {

                val fullname = full_name_profile_setting.text.toString()
                val username = username_profile_setting.text.toString()
                val bio = bio_profile_setting.text.toString()

                profileSettingViewModel.updateUserInfoOnly(
                    fullname,
                    username,
                    bio,
                    requireContext()
                )
            }
        }


    }

}
