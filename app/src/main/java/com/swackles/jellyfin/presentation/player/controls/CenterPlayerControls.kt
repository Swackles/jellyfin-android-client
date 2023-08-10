package com.swackles.jellyfin.presentation.player.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CenterPlayerControls(
    modifier: Modifier,
    isPlaying: Boolean,
    isLoading: Boolean,
    onRewind: () -> Unit,
    onPlayToggle: () -> Unit,
    onForward: () -> Unit
) {
    val color = if (!isLoading) Color.White else Color.Gray

    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.6f))) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(modifier = Modifier.size(40.dp), onClick = onRewind, enabled = !isLoading) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    tint = color
                )
            }

            IconButton(modifier = Modifier.size(40.dp), onClick = onPlayToggle, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (isPlaying) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Outlined.Pause,
                        contentDescription = "Pause",
                        tint = color
                    )
                } else {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Play",
                        tint = color
                    )
                }
            }

            IconButton(modifier = Modifier.size(40.dp), onClick = onForward, enabled = !isLoading) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Forward10,
                    contentDescription = "Replay 10 seconds",
                    tint = color
                )
            }
        }
    }
}