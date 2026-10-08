package com.example.triptogether

import android.app.Application
import com.example.triptogether.drive.DriveSession

class TripTogetherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DriveSession.context = applicationContext
    }
}
