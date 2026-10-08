package com.example.triptogether.data

import android.net.Uri
import com.example.triptogether.model.SharedItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.map

class FilesRepository : FilesDataSource {
    override fun items(tripId: String, photos: Boolean) =
        FirebaseDatabase.getInstance()
            .getReference(if (photos) "tripImages" else "tripDocuments")
            .child(tripId)
            .orderByChild("timestamp")
            .limitToLast(100)
            .snapshots()
            .map { snapshot ->
                snapshot.children
                    .mapNotNull { it.getValue(SharedItem::class.java) }
                    .sortedByDescending(SharedItem::timestamp)
            }

    override suspend fun upload(
        uri: Uri,
        mime: String,
        extension: String,
        creator: String,
        tripId: String,
        progress: (Int) -> Unit,
    ): Boolean {
        val user = checkNotNull(AuthRepository().currentUser)
        val api = com.example.triptogether.drive.DriveApi()
        val uploaded = api.upload(uri, mime, progress)
        try {
            FirebaseDatabase.getInstance()
                .getReference(if (mime.startsWith("image/")) "tripImages" else "tripDocuments")
                .child(tripId)
                .push()
                .setValue(
                    mapOf(
                        "name" to uploaded.name,
                        "url" to uploaded.url,
                        "uploadedBy" to user.email.orEmpty(),
                        "uploadedByUid" to user.uid,
                        "provider" to "drive",
                        "driveFileId" to uploaded.id,
                        "contentType" to mime,
                        "sizeBytes" to uploaded.size,
                        "timestamp" to com.google.firebase.database.ServerValue.TIMESTAMP,
                    )
                )
                .awaitResult()
        } catch (error: Exception) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                runCatching { kotlinx.coroutines.withTimeout(5_000) { api.delete(uploaded.id) } }
            }
            throw error
        }
        return mime.startsWith("image/")
    }
}
