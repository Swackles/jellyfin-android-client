package com.swackles.jellyfin.data.models

import androidx.media3.common.MediaItem

data class VideoItem(
    val name: String,
    val mediaItem: MediaItem
)