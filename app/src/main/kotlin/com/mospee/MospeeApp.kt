package com.mospee

import android.app.Application
import org.osmdroid.config.Configuration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MospeeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = packageName
    }
}
