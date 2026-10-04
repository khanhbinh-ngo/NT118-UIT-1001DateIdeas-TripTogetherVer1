package com.example.triptogether.utilities

import com.example.triptogether.BuildConfig

object Constants {
    val GEOCODING_API_KEY: String
        get() = BuildConfig.GEOCODING_API_KEY

    val MAPS_API_KEY: String
        get() = BuildConfig.MAPS_API_KEY
}
