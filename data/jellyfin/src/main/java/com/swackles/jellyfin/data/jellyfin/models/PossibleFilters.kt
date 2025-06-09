package com.swackles.jellyfin.data.jellyfin.models

class PossibleFilters(
    val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val ratings: List<String> = emptyList(),
    val years: List<Int> = emptyList()
)