package com.example.triptogether.utilities

import android.content.Intent
import androidx.core.content.IntentCompat
import com.example.triptogether.model.Trip

fun Intent.tripExtra(): Trip? = IntentCompat.getParcelableExtra(this, "trip", Trip::class.java)
