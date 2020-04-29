package com.x.myunn.ui.profile

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post
import de.hdodenhof.circleimageview.CircleImageView

class ProfileViewModel(c: Context) : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    val pref = c.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
    val profileId = pref.getString("profileId", "none").toString()

    //val profileId = ProfileFragment.newInstance().profileId


    var postList = myPost(profileId)

    fun myPost(profileId: String): MutableLiveData<MutableList<Post>> {

        val l_postList = MutableLiveData<MutableList<Post>>()

        firebaseRepo.myPost(profileId, l_postList)

        return l_postList
    }

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


    fun getTotalPosts(profileId: String, total_posts: TextView) {

        firebaseRepo.getTotalPosts(profileId, total_posts)
    }

    fun getTotalFollowers(profileId: String, total_followers: TextView?) {

        firebaseRepo.getTotalFollowers(profileId, total_followers)
    }

    fun getTotalFollowing(profileId: String, total_following: TextView?) {

        firebaseRepo.getTotalFollowing(profileId, total_following)
    }

    fun checkFollowingAndFollowButtonStatus(profileId: String, followBtn: Button) {
        firebaseRepo.checkFollowingAndFollowButtonStatus(profileId, followBtn)
    }

    fun follow(profileId: String) {

        firebaseRepo.follow(profileId)
    }

    fun unFollow(profileId: String) {

        firebaseRepo.unFollow(profileId)
    }
}
