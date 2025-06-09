package com.swackles.jellyfin.presentation.player.models

import androidx.media3.common.MediaItem

data class VideoItem(
    val name: String,
    val mediaItem: MediaItem
)