package com.x.unncrimewatch_k.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.x.myunn.ui.showUsers.ShowUsersViewModel


class ShowUsersViewModelFactory(val c: Context) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowUsersViewModel::class.java)) {
            return ShowUsersViewModel(c) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}