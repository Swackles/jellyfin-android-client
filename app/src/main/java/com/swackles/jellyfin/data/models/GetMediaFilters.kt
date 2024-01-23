package com.swackles.jellyfin.data.models

import com.swackles.jellyfin.data.enums.MediaItemType

data class GetMediaFilters(
    val genres: List<String> = emptyList(),
    val officialRatings: List<String> = emptyList(),
    val years: List<Int> = emptyList(),
    val mediaTypes: List<MediaItemType> = emptyList(),
    val query: String? = null
) {
}