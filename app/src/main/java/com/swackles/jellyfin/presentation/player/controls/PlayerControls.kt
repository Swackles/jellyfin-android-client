package com.swackles.jellyfin.presentation.player.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.swackles.jellyfin.presentation.player.PlayerUiState
import com.swackles.jellyfin.presentation.player.overlays.LanguageAndSubtitlesOverlay
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    state: PlayerUiState,
    goBack: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onPlayToggle: () -> Unit,
    onSeekChanged: (timeMs: Float) -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = state.isControlsVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
            TopControls(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                -fullHeight
                            }
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                goBack = goBack
            )
            CenterPlayerControls(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = state.isPlaying,
                isLoading = state.isLoading,
                onRewind = onRewind,
                onPlayToggle = onPlayToggle,
                onForward = onForward
            )

            BottomControls(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                isLoading = state.isLoading,
                isTvShow = state.isTvShow,
                isLastEpisode = state.isLastEpisode,
                totalDuration = state.totalDuration,
                currentTime = state.currentTime,
                bufferedPercentage = state.bufferedPercentage,
                onSeekChanged = onSeekChanged,
                toggleAudioAndSubtitlesOverlay = {},
                nextEpisode = {}
            )
        }
    }

    LanguageAndSubtitlesOverlay(
        showOverlay = state.isAudioAndSubtitleSelectVisible,
        activeAudio = 0,
        audios = state.audios,
        activeSubtitle = 0,
        subtitles = state.subtitles
    )
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewLoadingTvShowPlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = true,
        isTvShow = true,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewLoadingMoviePlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = true,
        isTvShow = false,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPlayingTvShowPlayerControls() {
    val state = PlayerUiState(
        isPlaying = true,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = true,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPlayingMoviePlayerControls() {
    val state = PlayerUiState(
        isPlaying = true,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = false,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPausedTvShowPlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = true,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPausedMoviePlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = false,
        isLastEpisode = false
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewLastEpisodeTvShowPlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = true,
        isLastEpisode = true
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewLastEpisodeMoviePlayerControls() {
    val state = PlayerUiState(
        isPlaying = false,
        totalDuration = 1000,
        currentTime = 255,
        bufferedPercentage = 50,
        isControlsVisible = true,
        isLoading = false,
        isTvShow = false,
        isLastEpisode = true
    )

    JellyfinTheme(true) {
        PlayerControls(
            state = state,
            goBack = {},
            onRewind = {},
            onPlayToggle = {},
            onForward = {},
            onSeekChanged = {  }
        )
    }
}
