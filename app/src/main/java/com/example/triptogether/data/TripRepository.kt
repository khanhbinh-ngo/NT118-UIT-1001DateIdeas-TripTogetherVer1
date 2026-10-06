package com.example.triptogether.data

import com.example.triptogether.model.Trip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.map

/** Creator references and canonical UID membership avoid global database reads. */
class TripRepository : TripDataSource {
    private val root
        get() = FirebaseDatabase.getInstance().reference

    override suspend fun loadTrips(): List<Trip> {
        val uid = AuthRepository().requireUid()
        val index = root.child("userTrips/$uid").get().awaitResult()
        val selected =
            root.child("favorites/$uid").get().awaitResult().children.mapNotNull { it.key }.toSet()
        return index.children.mapNotNull { entry ->
            val id = entry.key ?: return@mapNotNull null
            // Legacy boolean indexes refer to trips owned by the current user.
            val creator =
                (if (entry.value is String) entry.getValue(String::class.java) else null)
                    ?: if (entry.value == true) uid else return@mapNotNull null
            try {
                trip(creator, id)?.copy(isFavorite = id in selected)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (error.message?.contains("permission", ignoreCase = true) == true) null
                else throw error
            }
        }
    }

    override suspend fun trip(creator: String, id: String): Trip? {
        val snapshot = root.child("trips/$creator/$id").get().awaitResult()
        val item = snapshot.getValue(Trip::class.java) ?: return null
        val access = root.child("tripAccess/$id/members").get().awaitResult()
        val members =
            if (access.exists()) access.children.filter { it.value == true }.mapNotNull { it.key }
            else item.members
        return item.copy(id = id, createdBy = creator, members = members)
    }

    override fun favorites() =
        root.child("favorites/${AuthRepository().requireUid()}").snapshots().map { snapshot ->
            snapshot.children.mapNotNull { entry ->
                val id = entry.key ?: return@mapNotNull null
                val creator =
                    if (entry.value is String) entry.getValue(String::class.java)
                    else entry.child("createdBy").getValue(String::class.java)
                if (creator == null) return@mapNotNull null
                try {
                    trip(creator, id)?.copy(isFavorite = true)
                } catch (cancelled: kotlinx.coroutines.CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    if (error.message?.contains("permission", ignoreCase = true) == true) null
                    else throw error
                }
            }
        }

    override suspend fun favorite(trip: Trip, selected: Boolean) {
        val ref = root.child("favorites/${AuthRepository().requireUid()}/${trip.id}")
        (if (selected) ref.setValue(trip.createdBy) else ref.removeValue()).awaitResult()
    }

    override suspend fun isFavorite(id: String): Boolean =
        root
            .child("favorites")
            .child(AuthRepository().requireUid())
            .child(id)
            .get()
            .awaitResult()
            .exists()

    override suspend fun save(trip: Trip) {
        require(trip.createdBy == AuthRepository().requireUid())
        val members = (trip.members + trip.createdBy).distinct()
        val updates =
            mutableMapOf<String, Any?>(
                "trips/${trip.createdBy}/${trip.id}" to trip.copy(members = members),
                "tripAccess/${trip.id}" to
                    mapOf(
                        "creatorUid" to trip.createdBy,
                        "members" to members.associateWith { true },
                        "updatedAt" to com.google.firebase.database.ServerValue.TIMESTAMP,
                        "revision" to 0,
                    ),
            )
        members.forEach { updates["userTrips/$it/${trip.id}"] = trip.createdBy }
        root.updateChildren(updates).awaitResult()
    }

    override fun newId(): String = checkNotNull(root.child("trips").push().key)

    override suspend fun membership(creator: String, id: String, uid: String, adding: Boolean) {
        require(AuthRepository().requireUid() == creator) { "Only the owner can edit membership" }
        require(adding || uid != creator) { "The trip creator cannot be removed" }
        val updates =
            mutableMapOf<String, Any?>(
                "tripAccess/$id/members/$uid" to if (adding) true else null,
                "tripAccess/$id/updatedAt" to com.google.firebase.database.ServerValue.TIMESTAMP,
                "tripAccess/$id/revision" to com.google.firebase.database.ServerValue.increment(1),
                "userTrips/$uid/$id" to if (adding) creator else null,
            )
        if (!adding) {
            updates["favorites/$uid/$id"] = null
            updates["lastSeenChat/$uid/$id"] = null
        }
        root.updateChildren(updates).awaitResult()
    }

    override suspend fun unread(id: String): Boolean {
        val uid = AuthRepository().requireUid()
        val seen =
            root
                .child("lastSeenChat")
                .child(uid)
                .child(id)
                .get()
                .awaitResult()
                .getValue(Long::class.java) ?: 0L
        return root
            .child("chats")
            .child(id)
            .orderByChild("timestamp")
            .limitToLast(1)
            .get()
            .awaitResult()
            .children
            .any { (it.child("timestamp").getValue(Long::class.java) ?: 0L) > seen }
    }
}
