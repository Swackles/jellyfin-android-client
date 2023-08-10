package com.swackles.jellyfin.presentation.player.controls

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    isTvShow: Boolean,
    isLoading: Boolean,
    isLastEpisode: Boolean,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    toggleAudioAndSubtitlesOverlay: () -> Unit,
    nextEpisode: () -> Unit
) {

    Column(modifier = modifier.padding(start = 6.dp, end = 16.dp)) {
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
                    value = bufferedPercentage.toFloat(),
                    enabled = false,
                    onValueChange = { /*do nothing*/},
                    valueRange = 0f..bufferedPercentage.toFloat(),
                    colors =
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Color.White
                    )
                )

                Slider(
                    value = currentTime.toFloat(),
                    enabled = !isLoading,
                    onValueChange = onSeekChanged,
                    valueRange = 0f..totalDuration.toFloat(),
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

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                contentDescription = "Change audio and subtitles",
                icon = Icons.Outlined.Subtitles,
                onClick = toggleAudioAndSubtitlesOverlay,
                enabled = !isLoading
            )
            if (isTvShow) ActionButton(
                contentDescription = "Next episode",
                icon = Icons.Outlined.SkipNext,
                onClick = nextEpisode,
                enabled = !isLoading && !isLastEpisode
            )
        }
    }
}

@Composable
private fun ActionButton(
    contentDescription: String,
    enabled: Boolean,
    icon: ImageVector,
    onClick: () -> Unit) {
    IconButton(modifier = Modifier, onClick = onClick, interactionSource = NoRippleInteractionSource(), enabled = enabled) {
        Icon(
            modifier = Modifier.size(25.dp),
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else Color.Gray
        )
    }
}

private class NoRippleInteractionSource : MutableInteractionSource {

    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewLoadingTvShowPlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = true,
                isLoading = true,
                isLastEpisode = false,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewLoadingMoviePlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = false,
                isLoading = true,
                isLastEpisode = false,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewLastEpisodeTvShowPlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = true,
                isLoading = false,
                isLastEpisode = true,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewLastEpisodeMoviePlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = false,
                isLoading = false,
                isLastEpisode = true,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewTvShowPlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = true,
                isLoading = false,
                isLastEpisode = false,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 120)
private fun PreviewMoviePlayerBottomControls() {
    JellyfinTheme(true) {
        Surface {
            BottomControls(
                isTvShow = false,
                isLoading = false,
                isLastEpisode = false,
                totalDuration = 100,
                currentTime = 25,
                bufferedPercentage = 50,
                onSeekChanged = {  },
                toggleAudioAndSubtitlesOverlay = {  },
                nextEpisode = {  }
            )
        }
    }
}