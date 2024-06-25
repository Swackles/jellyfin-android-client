package com.swackles.jellyfin.presentation.player

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.swackles.jellyfin.data.jellyfin.repository.VideoMetadataReader
import dagger.hilt.android.lifecycle.HiltViewModel
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
    application: Application,
    final val metadataReader: VideoMetadataReader,
): AndroidViewModel(application) {
    val player: ExoPlayer
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

    init {
        player = ExoPlayer.Builder(application).build()
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

    override fun onCleared() {
        super.onCleared()

        player.removeListener(listener)
        player.release()
    }
}
