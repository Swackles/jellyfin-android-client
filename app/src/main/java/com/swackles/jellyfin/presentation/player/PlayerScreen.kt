package com.swackles.jellyfin.presentation.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import java.util.UUID

@Destination
@Composable
fun PlayerScreen(
    id: UUID,
    startPosition: Long,
    viewModal: PlayerViewModal = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()
    val videoItems by viewModal.videoItems.collectAsState()
    var lifecycle by remember {
        mutableStateOf(ON_CREATE)
    }

    LaunchedEffect(Unit){
        viewModal.playVideo(id)
    }

    DisposableEffect(key1 = true){
        systemUiController.isStatusBarVisible = false // Status bar
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            systemUiController.isStatusBarVisible = true // Status bar
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModal.player
                }
            },
            update = {
                when (lifecycle) {
                    Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }
                    Event.ON_RESUME -> {
                        it.onResume()
                    }
                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(videoItems.size) {
                Text(
                    text = videoItems[it].name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}