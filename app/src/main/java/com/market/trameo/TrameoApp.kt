package com.market.trameo

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrameoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this, mapOf(
            "cloud_name" to "drjp5ympc",
            "api_key" to "594814597849577",
            "api_secret" to "M4BtSYpm3JSEKtv2_AFQp8aWUsQ"
        ))
    }
}
