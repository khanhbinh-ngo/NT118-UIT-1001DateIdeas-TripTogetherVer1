package com.example.triptogether.drive

import org.junit.Assert.*
import org.junit.Test

class DriveLinksTest {
    @Test
    fun recognizesPrivateMediaWithoutTokenInMetadata() {
        val url = DriveLinks.media("sample_file-123")
        assertEquals("sample_file-123", DriveLinks.fileId(url))
        assertFalse(url.contains("token"))
    }

    @Test
    fun neverAuthorizesOtherOriginsOrCredentials() {
        listOf(
                "https://www.googleapis.com.attacker.example/drive/v3/files/id?alt=media",
                "https://attacker.example/drive/v3/files/id?alt=media",
                "https://user@www.googleapis.com/drive/v3/files/id?alt=media",
                "https://www.googleapis.com:8443/drive/v3/files/id?alt=media",
                "http://www.googleapis.com/drive/v3/files/id?alt=media",
                "https://www.googleapis.com/drive/v3/files/id/extra?alt=media",
            )
            .forEach { assertNull(DriveLinks.fileId(it)) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsPathInjection() {
        DriveLinks.media("../permissions")
    }
}
