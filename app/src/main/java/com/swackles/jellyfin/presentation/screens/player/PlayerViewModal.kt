package com.swackles.jellyfin.presentation.screens.player

import android.os.Parcelable
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import com.swackles.libs.jellyfin.MediaClient
import com.swackles.libs.jellyfin.PlaybackMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID
import javax.inject.Inject

sealed interface PlayerControlsState {
    val isVisible: Boolean

    data class Loading(override val isVisible: Boolean): PlayerControlsState

    sealed interface Playing: PlayerControlsState {
        val isPlaying: Boolean
        val totalDurationMs: Long
        val currentTimeMs: Long
        val bufferedPercentage: Float

        data class Movie(
            override val isVisible: Boolean,
            override val isPlaying: Boolean,
            override val totalDurationMs: Long,
            override val currentTimeMs: Long,
            override val bufferedPercentage: Float
        ): Playing

        data class Series(
            override val isVisible: Boolean,
            override val isPlaying: Boolean,
            override val totalDurationMs: Long,
            override val currentTimeMs: Long,
            override val bufferedPercentage: Float,
        ): Playing
    }
}

data class UiState(
    val playerControls: PlayerControlsState
): ViewState

@Parcelize
data class PlayerMediaItem(
    val id: UUID
): Parcelable

@HiltViewModel
class PlayerViewModal @Inject constructor(
    val player: Player,

    private val mediaClient: MediaClient
): BaseViewModel<UiState>() {
    override fun initialState(): UiState =
        UiState(playerControls = PlayerControlsState.Loading(isVisible = true))

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

    fun initialize(mediaItem: PlayerMediaItem) {
        player.prepare()

        player.addListener(listener)

        playVideo(mediaItem.id)
    }

    override fun onCleared() {
        super.onCleared()

        player.removeListener(listener)
        player.release()
    }

    fun toggleControls() {
        setState { copy(playerControls = when(playerControls) {
            is PlayerControlsState.Loading -> playerControls.copy(isVisible = !playerControls.isVisible)
            is PlayerControlsState.Playing.Movie -> playerControls.copy(isVisible = !playerControls.isVisible)
            is PlayerControlsState.Playing.Series -> playerControls.copy(isVisible = !playerControls.isVisible)
        }) }
    }

    fun togglePlay() {
        if (player.isPlaying) player.pause()
        else player.play()

    }

    fun seekTo(timeMs: Long) {
        player.seekTo(timeMs)
    }

    private fun playVideo(id: UUID, startPositionMs: Long = 0) = viewModelScope.launch {
        setStateLoading()
        player.playWhenReady = true

        val metadata = mediaClient.getPlaybackMetadata(id)

        player.setMediaItem(metadata.getMediaItem())
        player.seekTo(startPositionMs)

        setState { copy(
            playerControls = when (metadata) {
                is PlaybackMetadata.Series -> PlayerControlsState.Playing.Movie(
                    isVisible = state.value.playerControls.isVisible,
                    isPlaying = player.isPlaying,
                    totalDurationMs = player.duration.notLessThenZero(),
                    currentTimeMs = player.currentPosition.notLessThenZero(),
                    bufferedPercentage = player.bufferedPercentage / 100f
                )
                is PlaybackMetadata.Movie -> PlayerControlsState.Playing.Series(
                    isVisible = state.value.playerControls.isVisible,
                    isPlaying = player.isPlaying,
                    totalDurationMs = player.duration.notLessThenZero(),
                    currentTimeMs = player.currentPosition.notLessThenZero(),
                    bufferedPercentage = player.bufferedPercentage / 100f
                )
            }
        ) }
    }

    private fun updatePlayerData(player: Player) {
        setState { copy(playerControls = when(playerControls) {
            is PlayerControlsState.Loading -> {
                Log.d("PlayerViewModal", "Cannot update player controls as it is still loading")

                playerControls
            }
            is PlayerControlsState.Playing.Movie -> playerControls.copy(
                isPlaying = player.isPlaying,
                totalDurationMs = player.duration.notLessThenZero(),
                currentTimeMs = player.currentPosition.notLessThenZero(),
                bufferedPercentage = player.bufferedPercentage / 100f,
            )
            is PlayerControlsState.Playing.Series -> playerControls.copy(
                isPlaying = player.isPlaying,
                totalDurationMs = player.duration.notLessThenZero(),
                currentTimeMs = player.currentPosition.notLessThenZero(),
                bufferedPercentage = player.bufferedPercentage / 100f,
            )
        }) }
    }

    private fun setStateLoading() {
        setState { copy(PlayerControlsState.Loading(isVisible = state.value.playerControls.isVisible)) }
    }

    private fun Long.notLessThenZero(): Long =
        if(this < 0) 0L else this

    private fun PlaybackMetadata.getMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setUri(
                baseUrl.toUri().buildUpon()
                    .path("/Videos/$id/master.m3u8")
                    .appendQueryParameter("mediaSourceId", id.toString())
                    .build()
            )
            .build()
}
