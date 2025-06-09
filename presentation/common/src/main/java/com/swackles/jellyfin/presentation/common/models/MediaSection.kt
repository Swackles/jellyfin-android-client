package com.swackles.jellyfin.presentation.common.models

import com.swackles.jellyfin.data.jellyfin.models.LibraryItem

class MediaSection(
    val title: String,
    val medias: List<LibraryItem>
) {
    fun isEmpty() = this.medias.isEmpty()
}