package com.swackles.jellyfin.data.jellyfin.models

import com.swackles.jellyfin.data.jellyfin.enums.MediaItemType

data class MediaFilters(
    val genres: List<String> = emptyList(),
    val officialRatings: List<String> = emptyList(),
    val years: List<Int> = emptyList(),
    val mediaTypes: List<MediaItemType> = emptyList(),
    val query: String? = null
)