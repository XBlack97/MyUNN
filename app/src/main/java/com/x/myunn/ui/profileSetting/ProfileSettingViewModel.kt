package com.x.myunn.ui.profileSetting

import android.content.Context
import android.net.Uri
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo
import de.hdodenhof.circleimageview.CircleImageView

class ProfileSettingViewModel : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    fun loadUserInfo(
        profileId: String,
        profile_image_view_setting: CircleImageView,
        full_name_profile_setting: TextView,
        username_profile_setting: TextView,
        bio_profile_setting: TextView,
        c: Context
    ) {

        firebaseRepo.loadUserInfo(
            profileId, profile_image_view_setting, null, full_name_profile_setting,
            username_profile_setting, bio_profile_setting, c
        )

    }

    fun updateImageUserInfo(
        fullname: String,
        username: String,
        bio: String,
        imageUri: Uri,
        c: Context
    ) {

        firebaseRepo.updateImageUserInfo(fullname, username, bio, imageUri, c)

    }

    fun updateUserInfoOnly(fullname: String, username: String, bio: String, c: Context) {

        firebaseRepo.updateUserInfoOnly(fullname, username, bio, c)
    }

}
