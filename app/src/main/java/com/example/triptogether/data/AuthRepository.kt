package com.example.triptogether.data

import com.example.triptogether.model.SessionUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository : AuthDataSource {
    private val auth
        get() = FirebaseAuth.getInstance()

    override val currentUser: SessionUser?
        get() =
            auth.currentUser?.let {
                SessionUser(it.uid, it.email, it.displayName, it.photoUrl?.toString())
            }

    override fun requireUid(): String = checkNotNull(currentUser) { "Authentication required" }.uid

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).awaitResult()
    }

    override suspend fun register(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).awaitResult()
        checkNotNull(auth.currentUser)
            .updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
            .awaitResult()
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).awaitResult()
    }

    override suspend fun syncProfile() {
        UserRepository().ensureCurrentProfile()
    }
}
