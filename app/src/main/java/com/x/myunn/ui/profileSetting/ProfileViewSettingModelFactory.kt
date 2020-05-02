package com.x.myunn.ui.profileSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ProfileViewSettingModelFactory : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileSettingViewModel::class.java)) {
            return ProfileSettingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}