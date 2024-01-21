package com.swackles.jellyfin.presentation.common

open class StateHolder<T>(
    open val isLoading: Boolean = true,
    open val data: T? = null,
    open val error: String = ""
) {
    val hasData get() = data != null
    val hasError get() = error.isNotBlank()
}