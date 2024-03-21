package com.swackles.jellyfin.data.models

enum class AuthenticatorResponse {
    SUCCESS,
    INVALID_CREDENTIALS,
    INVALID_URL,
    UNKNOWN_ERROR,
    NO_USER
}