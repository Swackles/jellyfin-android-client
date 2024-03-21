package com.swackles.jellyfin.data.jellyfin.models

import com.swackles.jellyfin.data.jellyfin.enums.JellyfinResponses

data class JellyfinAuthResponse (
    val response: JellyfinResponses,
    val user: JellyfinUser?
)