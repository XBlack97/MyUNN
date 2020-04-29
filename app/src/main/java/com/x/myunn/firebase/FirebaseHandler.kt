package com.x.unncrimewatch_k.firebase

import android.app.Application
import com.google.firebase.database.FirebaseDatabase


class FirebaseHandler : Application() {


    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        println("FirebaseHandler: True")
    }

}