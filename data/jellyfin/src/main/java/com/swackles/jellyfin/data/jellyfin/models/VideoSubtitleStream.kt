package com.swackles.jellyfin.data.jellyfin.models

data class VideoSubtitleStream(
    val id: String,
    val label: String,
    val language: String,
    val url: String,
    val isDefault: Boolean
)
