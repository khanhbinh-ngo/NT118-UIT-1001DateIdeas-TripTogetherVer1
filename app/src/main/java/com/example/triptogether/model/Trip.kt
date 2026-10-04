package com.example.triptogether.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val members: List<String> = emptyList(),
    val createdBy: String = "",
    val isFavorite: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String? = null,
    @get:com.google.firebase.database.Exclude val hasUnreadMessages: Boolean = false,
) : Parcelable
