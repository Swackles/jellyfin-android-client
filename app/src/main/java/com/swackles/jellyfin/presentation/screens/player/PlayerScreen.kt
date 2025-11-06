@file:OptIn(ExperimentalAnimationApi::class)

package com.swackles.jellyfin.presentation.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.presentation.screens.player.controls.BottomControls
import com.swackles.jellyfin.presentation.screens.player.controls.CenterPlayerControls
import com.swackles.jellyfin.presentation.screens.player.controls.TopControls
import java.util.UUID

@RootNavGraph
@Destination
@Composable
fun PlayerScreen(
    mediaItem: PlayerMediaItem,
    viewModal: PlayerViewModal = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit){
        viewModal.initialize(mediaItem)
    }

    DisposableEffect(key1 = Unit){
        systemUiController.isStatusBarVisible = false
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            systemUiController.isStatusBarVisible = true
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clickable{ viewModal.toggleControls() },
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModal.player
                    it.useController = false
                }
            }
        )

        PlayerControls(
            state = viewModal.state.value.playerControls,
            goBack = { navigator.navigateUp() },
            onPlayToggle = { viewModal.togglePlay() },
            onSeekChanged = { viewModal.seekTo(it) }
        )
    }
}

@Composable
fun PlayerControls(
    state: PlayerControlsState,
    goBack: () -> Unit,
    onPlayToggle: () -> Unit,
    onSeekChanged: (timeMs: Long) -> Unit
) =
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
            TopControls(
                modifier = Modifier
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
                state = state,
                onPlayToggle = onPlayToggle,
                onSeekChanged = onSeekChanged
            )

            BottomControls(
                modifier = Modifier
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
                state = state,
                onSeekChanged = onSeekChanged
            )
        }
    }

@Composable
@Preview(name = "Loading", showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewLoading() {
    JellyfinTheme(true) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControls(
                state = PlayerControlsState.Loading(isVisible = true),
                goBack = {},
                onPlayToggle = {},
                onSeekChanged = {}
            )
        }
    }
}

@Composable
@Preview(name = "TV Show Playing", showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPlayingTvShow() {
    JellyfinTheme(true) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControls(
                state = PlayerControlsState.Playing.Series(
                    isVisible = true,
                    isPlaying = true,
                    totalDurationMs = 1000,
                    currentTimeMs = 255,
                    bufferedPercentage = .5f
                ),
                goBack = {},
                onPlayToggle = {},
                onSeekChanged = {}
            )
        }
    }
}

@Composable
@Preview(name = "Movie Playing", showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPlayingMovie() {
    JellyfinTheme(true) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControls(
                state = PlayerControlsState.Playing.Movie(
                    isVisible = true,
                    isPlaying = true,
                    totalDurationMs = 1000,
                    currentTimeMs = 255,
                    bufferedPercentage = .5f
                ),
                goBack = {},
                onPlayToggle = {},
                onSeekChanged = {}
            )
        }
    }
}

@Composable
@Preview(name = "TV Show Paused", showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPausedTvShow() {
    JellyfinTheme(true) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControls(
                state = PlayerControlsState.Playing.Series(
                    isVisible = true,
                    isPlaying = false,
                    totalDurationMs = 1000,
                    currentTimeMs = 255,
                    bufferedPercentage = .5f
                ),
                goBack = {},
                onPlayToggle = {},
                onSeekChanged = {}
            )
        }
    }
}

@Composable
@Preview(name = "Movie Paused", showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewPausedMovie() {
    JellyfinTheme(true) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerControls(
                state = PlayerControlsState.Playing.Movie(
                    isVisible = true,
                    isPlaying = true,
                    totalDurationMs = 1000,
                    currentTimeMs = 255,
                    bufferedPercentage = .5f
                ),
                goBack = {},
                onPlayToggle = {},
                onSeekChanged = {}
            )
        }
    }
}
