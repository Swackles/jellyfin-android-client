package com.swackles.jellyfin.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.data.jellyfin.models.Holder
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaBase
import com.swackles.jellyfin.data.jellyfin.repository.MediaRepositoryPreview
import com.swackles.jellyfin.data.useCase.GetDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

data class DetailScreenState(
    val data: DetailMediaBase? = null,
    val showOverlay: Boolean = false,
    val activeSeason: Int = 1,
    val isLoading: Boolean = false,
    val error: String = ""
)

@HiltViewModel
open class DetailScreenViewModal @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase
) : ViewModel() {
    private var _state by mutableStateOf(DetailScreenState(isLoading = true))

    open fun getState(): DetailScreenState {
        return _state
    }

    fun loadData(id: UUID) {
        getDetailUseCase(id).onEach { result ->
            when(result) {
                is Holder.Success -> {
                    val activeSeason = if (result.data!!.isSeries) result.data!!.getSeasons().first { it > 0 }
                        else 0

                    _state = DetailScreenState(
                        data = result.data,
                        activeSeason = activeSeason
                    )
                }
                is Holder.Error -> {
                    _state = DetailScreenState(
                        error = result.message ?: "Unexpected error",
                    )
                }
                is Holder.Loading -> {
                    _state = _state.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectSeason(season: Int) {
        _state = _state.copy(activeSeason = season, showOverlay = false)
    }

    fun toggleOverlay() {
        _state = _state.copy(showOverlay = !_state.showOverlay)
    }
}

class PreviewDetailScreenViewModal constructor(
    private val _state: DetailScreenState
) : DetailScreenViewModal(GetDetailUseCase(MediaRepositoryPreview())) {
    override fun getState() = _state
}