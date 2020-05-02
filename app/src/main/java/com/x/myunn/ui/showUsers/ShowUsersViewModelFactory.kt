package com.x.myunn.ui.showUsers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ShowUsersViewModelFactory(val c: Context, val safeArgs : ShowUsersFragmentArgs) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowUsersViewModel::class.java)) {
            return ShowUsersViewModel(c, safeArgs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}