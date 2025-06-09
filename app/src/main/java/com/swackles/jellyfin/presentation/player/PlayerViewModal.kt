package com.swackles.jellyfin.presentation.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.swackles.jellyfin.data.jellyfin.JellyfinClient
import com.swackles.jellyfin.presentation.player.extensions.getVideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

data class PlayerUiState(
    val isVisible: Boolean = true,
    val isLoading: Boolean = true,
    val isTvShow: Boolean = false,
    val isPlaying: Boolean = false,
    val isLastEpisode: Boolean = false,
    val totalDuration: Long = 100,
    val currentTime: Long = 0,
    val bufferedPercentage: Int = 0
)

@HiltViewModel
class PlayerViewModal @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val client: JellyfinClient
): ViewModel() {
    private val metadataReader = client.mediaService

    private val videoIds = savedStateHandle.getStateFlow("videoIds", emptyList<UUID>())
    private var episodes by mutableStateOf(emptyList<UUID>())
    private var _state by mutableStateOf(PlayerUiState())
    private val listener =
        object : Player.Listener {
            override fun onEvents(
                player: Player,
                events: Player.Events
            ) {
                super.onEvents(player, events)
                updatePlayerData(player)
            }
        }

    fun getState(): PlayerUiState = _state

    val videoItems = videoIds.map { ids ->
        ids.map { metadataReader.getMetadataUsingId(it).getVideoItem() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        player.prepare()

        player.addListener(listener)
    }

    fun updatePlayerData(player: Player) {
        _state = _state.copy(
            isPlaying = player.isPlaying,
            totalDuration = if (player.duration > 0) player.duration else 100,
            currentTime = player.currentPosition,
            bufferedPercentage = player.bufferedPercentage,
        )
    }

    suspend fun addVideoUri(id: UUID) {
        savedStateHandle["videoIds"] = videoIds.value + id
        player.addMediaItem(metadataReader.getMetadataUsingId(id).getVideoItem().mediaItem)
    }

    suspend fun playVideo(id: UUID, isTvShow: Boolean, startPosition: Long = 0) {
        _state = _state.copy(isLoading = true, isTvShow = isTvShow, isLastEpisode = episodes.lastOrNull() === id)
        player.playWhenReady = true
        val metadata = metadataReader.getMetadataUsingId(id)

        player.setMediaItem(metadata.getVideoItem().mediaItem)
        player.seekTo(startPosition / 10000)
        _state = _state.copy(isLoading = false)
    }

    override fun onCleared() {
        super.onCleared()

        player.removeListener(listener)
        player.release()
    }

    fun toggleControls() {
        _state = _state.copy(isVisible = !_state.isVisible)
    }

    fun togglePlay() {
        if (player.isPlaying) player.pause()
        else player.play()
    }

    fun seekTo(timeMs: Float) {
        player.seekTo(timeMs.toLong())
    }

    fun rewind() {
        player.seekBack()
    }

    fun forward() {
        player.seekForward()
    }
}
