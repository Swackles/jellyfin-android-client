package com.swackles.jellyfin.domain.auth.models

enum class AuthenticatorResponse {
    SUCCESS,
    INVALID_CREDENTIALS,
    INVALID_URL,
    UNKNOWN_ERROR,
    NO_USER,
    LOGOUT
}