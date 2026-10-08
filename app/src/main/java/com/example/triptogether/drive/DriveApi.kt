package com.example.triptogether.drive

import android.net.Uri
import android.provider.OpenableColumns
import com.example.triptogether.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** Private files are accessed with the current user's OAuth token, never a public link. */
class DriveApi {
    data class Uploaded(val id: String, val name: String, val size: Long, val mime: String) {
        val url
            get() = DriveLinks.media(id)
    }

    private fun connection(url: String, method: String): HttpURLConnection {
        val target = URL(url)
        require(
            target.protocol == "https" &&
                target.host == "www.googleapis.com" &&
                target.port in listOf(-1, 443) &&
                target.userInfo == null
        )
        return (target.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 30_000
            instanceFollowRedirects = false
            setRequestProperty("Authorization", "Bearer ${DriveSession.accessToken()}")
        }
    }

    private fun response(connection: HttpURLConnection): JSONObject {
        val status = connection.responseCode
        if (status !in 200..299) {
            if (status == 401) DriveSession.clear()
            throw IllegalStateException(
                "Drive request failed ($status). Check OAuth, folder permissions and quota."
            )
        }
        return connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
    }

    suspend fun verifyFolder() =
        withContext(Dispatchers.IO) {
            val id = BuildConfig.DRIVE_FOLDER_ID
            require(id.matches(Regex("[A-Za-z0-9_-]+"))) { "Drive folder is not configured" }
            val info =
                connection(
                    "https://www.googleapis.com/drive/v3/files/$id?fields=mimeType,capabilities(canAddChildren)&supportsAllDrives=true",
                    "GET",
                )
            try {
                val data = response(info)
                require(data.optString("mimeType") == "application/vnd.google-apps.folder")
                require(data.optJSONObject("capabilities")?.optBoolean("canAddChildren") == true) {
                    "The Google account needs Editor access to the test folder"
                }
            } finally {
                info.disconnect()
            }
            var next: String? = null
            do {
                val suffix =
                    next?.let { "&pageToken=" + java.net.URLEncoder.encode(it, "UTF-8") }.orEmpty()
                val permissions =
                    connection(
                        "https://www.googleapis.com/drive/v3/files/$id/permissions?fields=permissions(type),nextPageToken&supportsAllDrives=true$suffix",
                        "GET",
                    )
                try {
                    val data = response(permissions)
                    val list = data.optJSONArray("permissions") ?: JSONArray()
                    for (i in 0 until list.length()) if (
                        list.getJSONObject(i).optString("type") in listOf("anyone", "domain")
                    )
                        throw DrivePrivacyException()
                    next = data.optString("nextPageToken").takeIf { it.isNotBlank() }
                } finally {
                    permissions.disconnect()
                }
            } while (next != null)
        }

    suspend fun upload(uri: Uri, mime: String, progress: (Int) -> Unit = {}): Uploaded =
        withContext(Dispatchers.IO) {
            require(mime.startsWith("image/") || mime == "application/pdf")
            verifyFolder()
            val resolver = DriveSession.context.contentResolver
            var name = "upload"
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                cursor ->
                if (cursor.moveToFirst())
                    name = cursor.getString(0).orEmpty().ifBlank { "upload" }.take(180)
            }
            // Bounded staging supports unknown-length content providers without retaining an
            // Activity.
            val staged =
                java.io.File.createTempFile("drive-upload-", ".tmp", DriveSession.context.cacheDir)
            try {
                val maximum = if (mime.startsWith("image/")) 10 * 1024 * 1024 else 20 * 1024 * 1024
                resolver.openInputStream(uri)?.use { input ->
                    staged.outputStream().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var size = 0L
                        while (true) {
                            val count = input.read(buffer)
                            if (count < 0) break
                            size += count
                            require(size <= maximum) { "File too large" }
                            output.write(buffer, 0, count)
                        }
                    }
                } ?: throw IllegalArgumentException("Cannot read selected file")
                require(staged.length() > 0)
                val init =
                    connection(
                        "https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&fields=id,name,size,mimeType&supportsAllDrives=true",
                        "POST",
                    )
                val session: String
                try {
                    init.doOutput = true
                    init.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    init.setRequestProperty("X-Upload-Content-Type", mime)
                    init.setRequestProperty("X-Upload-Content-Length", staged.length().toString())
                    init.outputStream.bufferedWriter().use {
                        it.write(
                            JSONObject()
                                .put("name", name)
                                .put("parents", JSONArray().put(BuildConfig.DRIVE_FOLDER_ID))
                                .toString()
                        )
                    }
                    require(init.responseCode in 200..299) {
                        "Drive upload could not start (${init.responseCode})"
                    }
                    session = checkNotNull(init.getHeaderField("Location"))
                } finally {
                    init.disconnect()
                }
                val upload = connection(session, "PUT")
                try {
                    upload.doOutput = true
                    upload.setFixedLengthStreamingMode(staged.length())
                    upload.setRequestProperty("Content-Type", mime)
                    staged.inputStream().use { input ->
                        upload.outputStream.use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var sent = 0L
                            while (true) {
                                val count = input.read(buffer)
                                if (count < 0) break
                                output.write(buffer, 0, count)
                                sent += count
                                progress((sent * 100 / staged.length()).toInt().coerceAtMost(99))
                            }
                        }
                    }
                    val result = response(upload)
                    progress(100)
                    Uploaded(
                        result.getString("id"),
                        result.optString("name", name),
                        staged.length(),
                        mime,
                    )
                } finally {
                    upload.disconnect()
                }
            } finally {
                staged.delete()
            }
        }

    suspend fun delete(id: String) =
        withContext(Dispatchers.IO) {
            require(id.matches(Regex("[A-Za-z0-9_-]+")))
            val request =
                connection(
                    "https://www.googleapis.com/drive/v3/files/$id?supportsAllDrives=true",
                    "DELETE",
                )
            try {
                require(request.responseCode in 200..299)
            } finally {
                request.disconnect()
            }
        }
}
