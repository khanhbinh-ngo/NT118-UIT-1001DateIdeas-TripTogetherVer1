package com.example.triptogether.model

data class SharedItem(
    val name: String = "",
    val url: String = "",
    val uploadedBy: String = "",
    val timestamp: Long = 0L,
    val uploadedByUid: String = "",
    val storagePath: String = "",
    val contentType: String = "",
    val sizeBytes: Long = 0L,
    val provider: String = "",
    val driveFileId: String = "",
)
