package com.swackles.jellyfin.data.jellyfin.models

class MediaSection(
    val title: String,
    val medias: List<Media>
) {
    fun isEmpty() = this.medias.isEmpty()
}