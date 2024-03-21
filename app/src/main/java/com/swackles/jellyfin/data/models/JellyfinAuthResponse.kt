package com.swackles.jellyfin.data.models

import com.swackles.jellyfin.data.enums.JellyfinResponses

data class JellyfinAuthResponse (
    val response: JellyfinResponses,
    val user: JellyfinUser?
)