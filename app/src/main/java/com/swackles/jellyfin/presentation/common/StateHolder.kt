package com.swackles.jellyfin.presentation.common

open class StateHolder<T>(
    open val isLoading: Boolean = false,
    open val data: T? = null,
    open val error: String = ""
)