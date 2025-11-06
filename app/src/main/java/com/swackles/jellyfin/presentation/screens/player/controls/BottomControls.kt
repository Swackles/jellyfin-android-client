package com.swackles.jellyfin.presentation.screens.player.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.swackles.jellyfin.presentation.screens.player.PlayerControlsState
import com.swackles.jellyfin.presentation.styles.Spacings

@Composable
fun BoxScope.BottomControls(
    modifier: Modifier = Modifier,
    state: PlayerControlsState,
    onSeekChanged: (timeMs: Long) -> Unit
) {
    val bufferedPercentage = when(state) {
        is PlayerControlsState.Loading -> 0f
        is PlayerControlsState.Playing -> state.bufferedPercentage
    }
    val isPlaying = state is PlayerControlsState.Playing
    val currentTimeMs = when(state) {
        is PlayerControlsState.Loading -> 0f
        is PlayerControlsState.Playing -> state.currentTimeMs.toFloat()
    }
    val totalDurationMs = when(state) {
        is PlayerControlsState.Loading -> 0f
        is PlayerControlsState.Playing -> state.totalDurationMs.toFloat()
    }

    Column(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(horizontal = Spacings.Medium)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(2f)) {
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = 1f,
                    enabled = false,
                    onValueChange = { /*do nothing*/},
                    valueRange = 0f..1f,
                    colors =
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent
                    )
                )
                Slider(
                    value = bufferedPercentage,
                    enabled = false,
                    onValueChange = { /*do nothing*/ },
                    valueRange = 0f..1f,
                    colors =
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Color.White
                    )
                )
                Slider(
                    value = currentTimeMs,
                    enabled = isPlaying,
                    onValueChange = { onSeekChanged(it.toLong()) },
                    valueRange = 0f..totalDurationMs,
                    colors =
                    SliderDefaults.colors(
                        inactiveTrackColor = Color.Transparent,
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTickColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "15:06",
                color = Color.White
            )
        }
    }
}
