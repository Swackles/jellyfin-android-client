package com.swackles.jellyfin.data.jellyfin.models

import androidx.media3.common.MediaItem

data class VideoItem(
    val name: String,
    val mediaItem: MediaItem
)