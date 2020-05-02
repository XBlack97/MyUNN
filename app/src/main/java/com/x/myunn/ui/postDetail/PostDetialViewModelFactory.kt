package com.x.myunn.ui.postDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class PostDetialViewModelFactory(val c: Context, val safeArgs : PostDetailFragmentArgs) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(c, safeArgs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}