package com.example.triptogether.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.triptogether.R
import com.example.triptogether.model.SharedItem
import java.util.Locale

fun Context.openSharedDocument(document: SharedItem) {
    val driveId = com.example.triptogether.drive.DriveLinks.fileId(document.url)
    if (driveId != null) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(com.example.triptogether.drive.DriveLinks.view(driveId)),
                )
            )
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.file_viewer_missing, Toast.LENGTH_SHORT).show()
        }
        return
    }
    val uri = Uri.parse(document.url)
    if (uri.scheme != "https") {
        Toast.makeText(this, R.string.file_open_failed, Toast.LENGTH_SHORT).show()
        return
    }
    val extension = document.name.substringAfterLast('.', "").lowercase(Locale.ROOT)
    val mime =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
    try {
        startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.file_viewer_missing, Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, R.string.file_open_failed, Toast.LENGTH_SHORT).show()
    }
}
