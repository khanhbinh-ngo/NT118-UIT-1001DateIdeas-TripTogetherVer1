package com.example.triptogether.drive

import android.content.Context
import com.example.triptogether.data.AuthRepository

class DriveConnectionRequired :
    IllegalStateException("Connect Google Drive in Account before using files")

class DrivePrivacyException : IllegalStateException("The test folder must use Restricted access")

object DriveSession {
    lateinit var context: Context
    private var token: String? = null
    private var uid: String? = null
    private var expiresAt = 0L

    @Synchronized
    fun connect(value: String) {
        token = value
        uid = AuthRepository().requireUid()
        expiresAt = System.currentTimeMillis() + 50 * 60 * 1000
    }

    @Synchronized
    fun clear() {
        token = null
        uid = null
        expiresAt = 0
    }

    @Synchronized
    fun accessToken(): String {
        if (uid != AuthRepository().currentUser?.uid || System.currentTimeMillis() >= expiresAt)
            clear()
        return token ?: throw DriveConnectionRequired()
    }
}
