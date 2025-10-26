package com.swackles.jellyfin.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

interface ViewState

abstract class BaseViewModel<UiState: ViewState>: ViewModel() {
    protected abstract fun initialState(): UiState

    private val _state: MutableState<UiState> by lazy { mutableStateOf(initialState()) }
    open val state: State<UiState> by lazy { _state }

    protected fun setState(reducer: UiState.() -> UiState) {
        val newState = state.value.reducer()
        _state.value = newState
    }
}