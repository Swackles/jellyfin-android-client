package com.swackles.jellyfin.domain.common.models

sealed class Holder<T> {
    class Success<T>(val data: T) : Holder<T>()
    class Error<T>(val message: String) : Holder<T>()
    class Loading<T> : Holder<T>()
}
