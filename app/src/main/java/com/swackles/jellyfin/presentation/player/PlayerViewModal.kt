package com.swackles.jellyfin.presentation.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.swackles.jellyfin.data.models.MediaStreams
import com.swackles.jellyfin.data.repository.VideoMetadataReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

data class PlayerUiState(
    val isControlsVisible: Boolean = true,
    val isAudioAndSubtitleSelectVisible: Boolean = false,
    val isLoading: Boolean = true,
    val isTvShow: Boolean = false,
    val isPlaying: Boolean = false,
    val isLastEpisode: Boolean = false,
    val totalDuration: Long = 100,
    val currentTime: Long = 0,
    val bufferedPercentage: Int = 0,
    val subtitles: List<MediaStreams> = emptyList(),
    val audios: List<MediaStreams> = emptyList()
)

@HiltViewModel
class PlayerViewModal @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metadataReader: VideoMetadataReader
): ViewModel() {
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
        player.addMediaItem(metadataReader.getMetadataUsingId(id).getMediaItem())
    }

    suspend fun playVideo(id: UUID, isTvShow: Boolean, startPosition: Long = 0) {
        _state = _state.copy(isLoading = true, isTvShow = isTvShow, isLastEpisode = episodes.lastOrNull() === id)
        player.playWhenReady = true
        val metadata = metadataReader.getMetadataUsingId(id)

        player.setMediaItem(metadata.getMediaItem())
        player.seekTo(startPosition / 10000)
        _state = _state.copy(
            isLoading = false,
            subtitles = metadata.getSubtitles(),
            audios = metadata.getAudios()
        )
    }

    override fun onCleared() {
        super.onCleared()

        player.removeListener(listener)
        player.release()
    }

    fun toggleControls() {
        _state = _state.copy(isControlsVisible = !_state.isControlsVisible)
    }

    fun toggleAudioAndSubtitle() {
        _state = _state.copy(isControlsVisible = !_state.isControlsVisible, isAudioAndSubtitleSelectVisible = !_state.isAudioAndSubtitleSelectVisible)
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
