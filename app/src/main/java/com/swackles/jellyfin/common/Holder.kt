package com.swackles.jellyfin.common

sealed class Holder<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Holder<T>(data)
    class Error<T>(message: String) : Holder<T>(null, message)
    class Loading<T> : Holder<T>()
}