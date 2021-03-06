package com.x.myunn.ui.profileSetting

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo

class ProfileSettingViewModel : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    fun loadUserInfo(
        profileId: String,
        profile_image_view_setting: ImageView,
        full_name_profile_setting: TextView,
        username_profile_setting: TextView,
        bio_profile_setting: TextView,
        c: Context
    ) {

        firebaseRepo.loadUserInfo(
            profileId, profile_image_view_setting, full_name_profile_setting,
            username_profile_setting, bio_profile_setting, c
        )

    }

    fun updateUserInfo(
        fullname: String,
        username: String,
        bio: String?,
        imageUri: Uri?,
        c: Context
    ) {

        firebaseRepo.updateUserInfo(fullname, username, bio, imageUri, c)

    }
}
