package com.swackles.jellyfin.presentation.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.presentation.common.Overlay
import com.swackles.jellyfin.presentation.common.components.H2
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.player.controls.PlayerControls
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme
import java.util.UUID

@Destination
@Composable
fun PlayerScreen(
    id: UUID,
    startPosition: Long,
    isTvShow: Boolean = false,
    viewModal: PlayerViewModal = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit){
        viewModal.playVideo(id, isTvShow, startPosition)
    }

    DisposableEffect(key1 = Unit){


        systemUiController.isStatusBarVisible = false // Status bar
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            systemUiController.isStatusBarVisible = true // Status bar
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clickable {
                    viewModal.toggleControls()
                },
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModal.player
                    it.useController = false
                }
            }
        )

        PlayerControls(
            state = viewModal.getState(),
            goBack = { navigator.navigateUp() },
            onRewind = { viewModal.rewind() },
            onPlayToggle = { viewModal.togglePlay() },
            onForward = { viewModal.forward() },
            onSeekChanged = { viewModal.seekTo(it) }
        )
    }
}
