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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.x.myunn.R
import com.x.myunn.activities.LogInActivity
import com.x.myunn.firebase.FirebaseRepo
import com.x.unncrimewatch_k.ui.profile.ProfileSettingViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.fragment_profile_setting.view.*

class ProfileSettingFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileSettingFragment()
    }

    private lateinit var profileSettingViewModel: ProfileSettingViewModel
    val firebaseRepo = FirebaseRepo()

    val mAuth = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUri = ""
    private lateinit var imageUri: Uri
    private var storageProfilePicRef: StorageReference? = null

    lateinit var navController: NavController
    lateinit var mainProfileImageView: CircleImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile_setting, container, false)

        val bnv = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bnv.visibility = View.GONE

        navController = findNavController(requireActivity(), R.id.nav_host_fragment)


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
            navController.navigate(R.id.action_profileSettingFragment_to_nav_profile)
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
            ViewModelProviders.of(this, viewModelFactory).get(ProfileSettingViewModel::class.java)

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

            println("Image: $imageUri")
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

                //navController.navigate(R.id.action_profileFragment_to_navigation_home)
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

                //navController.navigate(R.id.action_profileFragment_to_navigation_home)
            }
        }


    }

//    private fun userInfo(user: User) {
//
//
////                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile)
////                        .into(profile_image_view_setting)
////                    username_profile_setting.setText(user.username)
////                    full_name_profile_setting.setText(user.fullname)
////                    bio_profile_setting.setText(user.bio)
//
//        Glide.with(context!!)
//            .load(user.image)
//            .apply(
//                RequestOptions()
//                .placeholder(R.drawable.loading_animation)
//                .error(R.drawable.ic_broken_image))
//            .into(profile_image_view_setting)
//
//
//        username_profile_setting.setText(user.username)
//        full_name_profile_setting.setText(user.fullname)
//        bio_profile_setting.setText(user.bio)
//
//     }

}
