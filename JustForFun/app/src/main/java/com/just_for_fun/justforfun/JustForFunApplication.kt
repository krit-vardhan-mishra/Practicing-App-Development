package com.just_for_fun.justforfun

import android.app.Application
import com.google.firebase.FirebaseApp

class JustForFunApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}