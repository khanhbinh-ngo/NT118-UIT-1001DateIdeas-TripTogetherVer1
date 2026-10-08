package com.example.triptogether.drive

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.triptogether.R

object DriveImages {
    fun load(
        view: ImageView,
        url: String?,
        placeholder: Int = R.drawable.ic_image_placeholder,
        circle: Boolean = false,
        fit: Boolean = false,
    ) {
        val privateId = DriveLinks.fileId(url)
        val source: Any? =
            if (privateId != null) {
                runCatching {
                        GlideUrl(
                            url,
                            LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer ${DriveSession.accessToken()}")
                                .build(),
                        )
                    }
                    .getOrNull()
            } else url
        var request = Glide.with(view).load(source).placeholder(placeholder).error(placeholder)
        if (privateId != null)
            request = request.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
        request =
            if (circle) request.circleCrop()
            else if (fit) request.fitCenter() else request.centerCrop()
        request.into(view)
    }
}
