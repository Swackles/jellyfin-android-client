package com.swackles.jellyfin.presentation.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.common.Holder
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.domain.useCase.GetDetailUseCase
import com.swackles.jellyfin.presentation.common.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModal @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase
) : ViewModel() {
    private val _state = mutableStateOf(StateHolder<DetailMedia>())
    val state: State<StateHolder<DetailMedia>> = _state

    fun loadData(id: UUID) {
        println("loadData")
        getDetailUseCase(id).onEach { result ->
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