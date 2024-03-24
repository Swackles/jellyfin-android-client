package com.swackles.jellyfin.presentation.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.GetMediaFilters
import com.swackles.jellyfin.data.jellyfin.models.Media
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.repository.MediaRepositoryPreview
import com.swackles.jellyfin.data.useCase.GetFiltersUseCase
import com.swackles.jellyfin.data.useCase.GetMediaItemsUseCase
import com.swackles.jellyfin.presentation.common.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
open class SearchViewModal @Inject constructor(
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase
) : ViewModel() {
    private var _mediaFiltersState = mutableStateOf(StateHolder<MediaFilters>())
    open val mediaFiltersState: State<StateHolder<MediaFilters>> = _mediaFiltersState
    private var _mediaItemsState = mutableStateOf(StateHolder<List<Media>>())
    open val mediaItemsState: State<StateHolder<List<Media>>> = _mediaItemsState

    init {
        loadFilters()
    }

    fun getMediaItems(): List<Media> {
        return _mediaItemsState.value.data ?: emptyList()
    }

    fun searchItems(filters: GetMediaFilters) {
        getMediaItemsUseCase(filters).onEach { result ->
            when(result) {
                is Holder.Success -> {
                    _mediaItemsState.value = StateHolder(data = result.data)
                }
                is Holder.Error -> {
                    _mediaItemsState.value = StateHolder(error = result.message ?: "Unexpected error")
                }
                is Holder.Loading -> {
                    _mediaItemsState.value = StateHolder(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadFilters() {
        getFiltersUseCase().onEach { result ->
            when(result) {
                is Holder.Success -> {
                    _mediaFiltersState.value = StateHolder(data = result.data)
                }
                is Holder.Error -> {
                    _mediaFiltersState.value = StateHolder(error = result.message ?: "Unexpected error")
                }
                is Holder.Loading -> {
                    _mediaFiltersState.value = StateHolder(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}

class StatePreview<T>(override val value: T) : State<T>

class PreviewSearchViewModal constructor(
    filtersState: StateHolder<MediaFilters>,
    mediaItemsState: StateHolder<List<Media>>
) : SearchViewModal(GetFiltersUseCase(MediaRepositoryPreview()), GetMediaItemsUseCase(MediaRepositoryPreview())) {
    override val mediaFiltersState: State<StateHolder<MediaFilters>> = StatePreview(filtersState)
    override val mediaItemsState: State<StateHolder<List<Media>>> = StatePreview(mediaItemsState)
}