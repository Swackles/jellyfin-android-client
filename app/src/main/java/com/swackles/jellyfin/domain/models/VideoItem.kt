package com.swackles.jellyfin.domain.models

import androidx.media3.common.MediaItem

data class VideoItem(
    val name: String,
    val mediaItem: MediaItem
)