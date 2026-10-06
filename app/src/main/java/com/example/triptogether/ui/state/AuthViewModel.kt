package com.example.triptogether.ui.state

import com.example.triptogether.R
import com.example.triptogether.data.AuthDataSource
import com.example.triptogether.data.AuthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class AuthViewModel(private val repository: AuthDataSource = AuthRepository()) : ActionViewModel() {
    var failureMessage: Int = R.string.auth_failed
        private set

    fun submit(name: String?, email: String, password: String) = perform {
        failureMessage = R.string.auth_failed
        boundedRequest {
            if (name == null) repository.signIn(email, password)
            else repository.register(name, email, password)
        }
        syncProfile()
    }

    fun completeSignIn() = perform { syncProfile() }

    fun resetPassword(email: String) = perform {
        boundedRequest { repository.resetPassword(email) }
    }

    private suspend fun syncProfile() {
        failureMessage = R.string.profile_sync_failed
        boundedRequest { repository.syncProfile() }
    }

    private suspend fun boundedRequest(block: suspend () -> Unit) {
        try {
            withTimeout(20_000) { block() }
        } catch (timeout: TimeoutCancellationException) {
            // Treat request timeout as a recoverable error, preserving lifecycle cancellation.
            throw IllegalStateException("Authentication request timed out", timeout)
        }
    }
}
