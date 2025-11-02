package com.swackles.jellyfin.presentation.screens.detail

import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import com.swackles.libs.jellyfin.Episode
import com.swackles.libs.jellyfin.LibraryClient
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.MediaClient
import com.swackles.libs.jellyfin.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class UiState(
    val step: Step
): ViewState

sealed interface Step {
    object Loading: Step

    data class Success(
        val mediaItem: MediaItem,
        val similar: List<LibraryItem>,
        val episodes: Map<Int, List<Episode>>,
    ): Step
}

@HiltViewModel
class DetailScreenViewModal @Inject constructor(
    private val libraryClient: LibraryClient,
    private val mediaClient: MediaClient
): BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)

    fun loadData(id: UUID) = viewModelScope.launch { loadAllData(id) }

    fun loadAllData(id: UUID) = viewModelScope.launch {
        val mediaItemDeferred = async(Dispatchers.IO) { mediaClient.getItem(id) }
        val episodesDeferred = async(Dispatchers.IO) { mediaClient.getEpisodes(id) }
        val similarDeferred = async(Dispatchers.IO) { libraryClient.getSimilar(id) }

        setStep(Step.Success(
            mediaItem = mediaItemDeferred.await(),
            similar = similarDeferred.await(),
            episodes = episodesDeferred.await(),

        ))
    }

    private fun setStep(step: Step) = setState { copy(step = step) }

}
