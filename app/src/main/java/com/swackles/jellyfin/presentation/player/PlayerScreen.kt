package com.swackles.jellyfin.presentation.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.presentation.player.JellyfinPlayer
import com.swackles.presentation.player.JellyfinPlayerUserContext
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
    val activeUser = viewModal.activeUser.value

    val player = JellyfinPlayer(LocalContext.current, JellyfinPlayerUserContext(activeUser.token, activeUser.deviceId), viewModal.metadataReader)
    val systemUiController = rememberSystemUiController()
    
    LaunchedEffect(key1 = id) {
        player.addMedia(id)
    }

    DisposableEffect(key1 = Unit){
        systemUiController.isStatusBarVisible = false // Status bar
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            player.release()
            systemUiController.isStatusBarVisible = true // Status bar
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            factory = { context ->
                PlayerView(context).also {
                    it.player = player.player
                }
            }
        )
    }
}