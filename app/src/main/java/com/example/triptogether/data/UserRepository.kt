package com.example.triptogether.data

import android.net.Uri
import com.example.triptogether.model.UserProfile
import com.google.firebase.database.FirebaseDatabase

class UserRepository : UserDataSource {
    private val users
        get() = FirebaseDatabase.getInstance().getReference("users")

    override suspend fun profile(uid: String): UserProfile {
        val snapshot = users.child(uid).get().awaitResult()
        val name = snapshot.child("name").getValue(String::class.java).orEmpty().trim()
        return UserProfile(
            uid,
            snapshot.child("email").getValue(String::class.java).orEmpty(),
            snapshot.child("firstName").getValue(String::class.java) ?: name.substringBefore(" "),
            snapshot.child("lastName").getValue(String::class.java) ?: name.substringAfter(" ", ""),
            snapshot.child("role").getValue(String::class.java).orEmpty(),
            snapshot.child("imageUrl").getValue(String::class.java)?.takeIf(String::isNotBlank)
                ?: snapshot.child("photoUrl").getValue(String::class.java),
        )
    }

    override suspend fun findUid(email: String): String? =
        users
            .orderByChild("emailNormalized")
            .equalTo(email.trim().lowercase(java.util.Locale.ROOT))
            .limitToFirst(1)
            .get()
            .awaitResult()
            .children
            .firstOrNull()
            ?.key

    override suspend fun save(firstName: String, lastName: String, role: String, photo: Uri?) {
        val uid = AuthRepository().requireUid()
        val updates =
            mutableMapOf<String, Any?>(
                "name" to "$firstName $lastName".trim(),
                "firstName" to firstName,
                "lastName" to lastName,
                "role" to role,
            )
        if (photo != null) {
            val mime =
                com.example.triptogether.drive.DriveSession.context.contentResolver
                    .getType(photo)
                    .orEmpty()
            require(mime.startsWith("image/"))
            updates["imageUrl"] = com.example.triptogether.drive.DriveApi().upload(photo, mime).url
        }
        users.child(uid).updateChildren(updates).awaitResult()
    }

    override suspend fun ensureCurrentProfile() {
        val user = checkNotNull(AuthRepository().currentUser)
        val ref = users.child(user.uid)
        if (!ref.get().awaitResult().exists()) {
            ref.setValue(
                    mapOf(
                        "email" to user.email,
                        "emailNormalized" to user.email?.lowercase(java.util.Locale.ROOT),
                        "name" to user.displayName.orEmpty(),
                        "photoUrl" to user.photoUrl?.toString().orEmpty(),
                    )
                )
                .awaitResult()
        } else {
            ref.updateChildren(
                    mapOf(
                        "email" to user.email,
                        "emailNormalized" to user.email?.lowercase(java.util.Locale.ROOT),
                    )
                )
                .awaitResult()
        }
    }
}
