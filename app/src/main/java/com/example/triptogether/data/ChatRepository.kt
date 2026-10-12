package com.example.triptogether.data

import com.example.triptogether.model.ChatMessage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.map

class ChatRepository : ChatDataSource {
    private val root
        get() = FirebaseDatabase.getInstance().reference

    override fun messages(tripId: String) =
        root
            .child("chats")
            .child(tripId)
            .orderByChild("timestamp")
            .limitToLast(100)
            .snapshots()
            .map { snapshot ->
                snapshot.children
                    .mapNotNull { it.getValue(ChatMessage::class.java) }
                    .sortedBy(ChatMessage::timestamp)
            }

    override suspend fun send(tripId: String, text: String) {
        val user = checkNotNull(AuthRepository().currentUser)
        root
            .child("chats")
            .child(tripId)
            .push()
            .setValue(
                mapOf(
                    "sender" to user.email.orEmpty(),
                    "senderUid" to user.uid,
                    "message" to text.trim(),
                    "timestamp" to com.google.firebase.database.ServerValue.TIMESTAMP,
                )
            )
            .awaitResult()
    }

    override suspend fun markSeen(tripId: String, timestamp: Long) {
        root
            .child("lastSeenChat")
            .child(AuthRepository().requireUid())
            .child(tripId)
            .setValue(timestamp)
            .awaitResult()
    }
}
