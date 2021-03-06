package com.x.myunn.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ProfileViewModelFactory(val c: Context, val safeArgs : ProfileFragmentArgs) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(c, safeArgs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}