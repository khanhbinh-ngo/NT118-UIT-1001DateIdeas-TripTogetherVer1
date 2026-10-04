package com.example.triptogether.data

import android.net.Uri
import com.example.triptogether.model.ActivityItem
import com.example.triptogether.model.ChatMessage
import com.example.triptogether.model.SessionUser
import com.example.triptogether.model.SharedItem
import com.example.triptogether.model.Trip
import com.example.triptogether.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** Repository contracts allow fake data sources in the subsequent testing phase. */
interface AuthDataSource {
    val currentUser: SessionUser?

    fun requireUid(): String

    suspend fun signIn(email: String, password: String)

    suspend fun register(name: String, email: String, password: String)

    suspend fun resetPassword(email: String)

    suspend fun syncProfile()
}

interface UserDataSource {
    suspend fun profile(uid: String): UserProfile

    suspend fun findUid(email: String): String?

    suspend fun save(firstName: String, lastName: String, role: String, photo: Uri?)

    suspend fun ensureCurrentProfile()
}

interface TripDataSource {
    suspend fun loadTrips(): List<Trip>

    suspend fun trip(creator: String, id: String): Trip?

    fun favorites(): Flow<List<Trip>>

    suspend fun favorite(trip: Trip, selected: Boolean)

    suspend fun isFavorite(id: String): Boolean

    suspend fun save(trip: Trip)

    fun newId(): String

    suspend fun membership(creator: String, id: String, uid: String, adding: Boolean)

    suspend fun unread(id: String): Boolean
}

interface PlanDataSource {
    suspend fun activities(
        creator: String,
        tripId: String,
        index: Int,
    ): List<Pair<ActivityItem, String>>

    suspend fun save(creator: String, tripId: String, index: Int, key: String?, item: ActivityItem)

    suspend fun delete(creator: String, tripId: String, index: Int, key: String)
}

interface ChatDataSource {
    fun messages(tripId: String): Flow<List<ChatMessage>>

    suspend fun send(tripId: String, text: String)

    suspend fun markSeen(tripId: String, timestamp: Long)
}

interface FilesDataSource {
    fun items(tripId: String, photos: Boolean): Flow<List<SharedItem>>

    suspend fun upload(
        uri: Uri,
        mime: String,
        extension: String,
        creator: String,
        tripId: String,
        progress: (Int) -> Unit,
    ): Boolean
}

interface CreateTripDataSource {
    suspend fun create(
        name: String,
        country: String,
        start: String,
        end: String,
        emails: List<String>,
        photo: Uri?,
    )
}
