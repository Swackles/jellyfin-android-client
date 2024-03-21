package com.swackles.jellyfin.presentation.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.useCase.BaseUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseScreenViewModal<T>(
    private val getUseCase: BaseUseCase<T>
) : ViewModel() {
    private val _state = mutableStateOf(StateHolder<T>())
    open val state: State<StateHolder<T>> = _state

    init {
        loadData()
    }

    private fun loadData() {
        getUseCase().onEach { result ->
            when(result) {
                is Holder.Success -> {
                    println("LOADED DATA - SUCCESS")
                    _state.value = StateHolder(data = result.data)
                }
                is Holder.Error -> {
                    println("LOADED DATA - ERROR \"" + result.message + "\" ")
                    _state.value = StateHolder(error = result.message ?: "Unexpected error")
                }
                is Holder.Loading -> {
                    println("LOADED DATA - LOADING")
                    _state.value = StateHolder(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}