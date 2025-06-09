package com.swackles.jellyfin.presentation.player.extensions

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.swackles.jellyfin.data.jellyfin.models.VideoMetadata
import com.swackles.jellyfin.presentation.player.models.VideoItem

fun VideoMetadata.getVideoItem(): VideoItem =
    VideoItem(
        name = metadata.mediaSources.first().name ?: "Unknown Name",
        mediaItem = MediaItem.Builder()
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setUri(
                Uri.parse(baseUrl).buildUpon()
                    .path("/Videos/$id/master.m3u8")
                    .appendQueryParameter("mediaSourceId", id.toString())
                    .build()
            )
            .build()
    )
