package com.swackles.auth.models

import com.swackles.auth.enums.ErrorKey

data class ServerLoginFormResponseState(
    val errors: Map<ErrorKey, String> = emptyMap(),
    val isLoading: Boolean = false
)
