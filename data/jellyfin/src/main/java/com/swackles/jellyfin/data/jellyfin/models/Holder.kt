package com.swackles.jellyfin.data.jellyfin.models

sealed class Holder<T>(isLoading: Boolean = true, val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Holder<T>(false, data)
    class Error<T>(message: String) : Holder<T>(false, message = message)
    class Loading<T> : Holder<T>(true)
}