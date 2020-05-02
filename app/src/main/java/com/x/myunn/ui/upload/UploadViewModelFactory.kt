package com.x.myunn.ui.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UploadViewModelFactory : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}