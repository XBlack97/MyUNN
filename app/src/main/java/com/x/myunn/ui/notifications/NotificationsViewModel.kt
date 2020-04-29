package com.x.myunn.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.x.myunn.firebase.FirebaseRepo
import com.x.myunn.model.Notification

class NotificationsViewModel : ViewModel() {


    val firebaseRepo = FirebaseRepo()

    val notifications = loadNotifications()


    fun loadNotifications(): MutableLiveData<MutableList<Notification>> {

        val _notifications = MutableLiveData<MutableList<Notification>>()

        firebaseRepo.readNotifications(_notifications)

        return _notifications
    }
}