package com.swackles.jellyfin.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.swackles.jellyfin.data.repository.VideoMetadataReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PlayerViewModal @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metadataReader: VideoMetadataReader
): ViewModel() {
    private val videoIds = savedStateHandle.getStateFlow("videoIds", emptyList<UUID>())

    val videoItems = videoIds.map { ids ->
        ids.map { metadataReader.getMetadataUsingId(it).getVideoItem() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        player.prepare()

    }

    suspend fun addVideoUri(id: UUID) {
        savedStateHandle["videoIds"] = videoIds.value + id
        player.addMediaItem(metadataReader.getMetadataUsingId(id).getMediaItem())
    }

    suspend fun playVideo(id: UUID, startPosition: Long = 0) {
        player.playWhenReady = true
        player.seekTo(startPosition)
        player.setMediaItem(metadataReader.getMetadataUsingId(id).getMediaItem())
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}