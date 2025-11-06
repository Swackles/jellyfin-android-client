package com.swackles.jellyfin.presentation.screens.player.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import com.swackles.jellyfin.presentation.screens.player.PlayerControlsState

@Composable
fun BoxScope.CenterPlayerControls(
    state: PlayerControlsState,
    onPlayToggle: () -> Unit,
    onSeekChanged: (timeMs: Long) -> Unit
) {
    val enabled = state !is PlayerControlsState.Loading
    val color = if (enabled) Color.White else Color.Gray

    Box(modifier = Modifier
        .align(Alignment.Center)
        .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier.size(40.dp),
                onClick = { onSeekChanged((state as PlayerControlsState.Playing).currentTimeMs - Constants.REWIND) },
                enabled = enabled
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    tint = color
                )
            }

            IconButton(modifier = Modifier.size(40.dp), onClick = onPlayToggle, enabled = enabled) {
                when(state) {
                    is PlayerControlsState.Loading -> CircularProgressIndicator()
                    is PlayerControlsState.Playing -> {
                        val icon = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow

                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = icon,
                            contentDescription = icon.name,
                            tint = color
                        )
                    }
                }
            }

            IconButton(
                modifier = Modifier.size(40.dp),
                onClick = { onSeekChanged((state as PlayerControlsState.Playing).currentTimeMs + Constants.FORWARD) },
                enabled = enabled
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Forward10,
                    contentDescription = "Forawrd 10 seconds",
                    tint = color
                )
            }
        }
    }
}

private object Constants {
    const val SECOND_TO_MS_MULTI = 1000
    val REWIND = 10 *  SECOND_TO_MS_MULTI
    val FORWARD = 10 *  SECOND_TO_MS_MULTI
}