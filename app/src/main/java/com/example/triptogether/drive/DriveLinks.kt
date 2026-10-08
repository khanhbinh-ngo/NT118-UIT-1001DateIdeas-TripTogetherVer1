package com.example.triptogether.drive

import java.net.URI

object DriveLinks {
    private val validId = Regex("[A-Za-z0-9_-]+")

    fun media(id: String): String {
        require(validId.matches(id))
        return "https://www.googleapis.com/drive/v3/files/$id?alt=media"
    }

    fun fileId(url: String?): String? =
        runCatching {
                val uri = URI(url ?: return null)
                if (
                    uri.scheme != "https" ||
                        uri.host != "www.googleapis.com" ||
                        uri.port != -1 ||
                        uri.userInfo != null
                )
                    return null
                val id = uri.path.removePrefix("/drive/v3/files/")
                if (
                    uri.path != "/drive/v3/files/$id" ||
                        !validId.matches(id) ||
                        uri.query != "alt=media"
                )
                    null
                else id
            }
            .getOrNull()

    fun view(id: String): String {
        require(validId.matches(id))
        return "https://drive.google.com/file/d/$id/view"
    }
}
