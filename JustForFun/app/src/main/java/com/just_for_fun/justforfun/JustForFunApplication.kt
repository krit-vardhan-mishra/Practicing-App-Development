package com.just_for_fun.justforfun

import android.app.Application
import com.just_for_fun.justforfun.data.managers.UserManager

class JustForFunApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize UserManager for global state management
        UserManager.getInstance().initialize(this)
    }
}