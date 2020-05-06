package com.x.myunn.ui.saves

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Post

class SavesViewModel : ViewModel() {

    val firebaseRepo = FirebaseRepo()

    var savedPostList = loadSavedPosts()

    private fun loadSavedPosts(): MutableLiveData<MutableList<Post>> {

        val l_savedPostList = MutableLiveData<MutableList<Post>>()

        firebaseRepo.saves(l_savedPostList)

        return l_savedPostList
    }
}