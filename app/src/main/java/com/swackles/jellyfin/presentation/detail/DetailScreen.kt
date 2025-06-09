package com.swackles.jellyfin.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.presentation.common.colors.primaryAssistChipBorder
import com.swackles.jellyfin.presentation.common.colors.primaryAssistChipColors
import com.swackles.jellyfin.presentation.common.colors.primaryButtonColors
import com.swackles.jellyfin.presentation.common.colors.primaryButtonContentPadding
import com.swackles.jellyfin.presentation.common.components.H2
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.preview.preview
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.presentation.destinations.DetailScreenDestination
import com.swackles.jellyfin.presentation.destinations.PlayerScreenDestination
import com.swackles.jellyfin.presentation.detail.components.BannerImage
import com.swackles.jellyfin.presentation.detail.components.LogoImage
import com.swackles.jellyfin.presentation.detail.extensions.getInfo
import com.swackles.jellyfin.presentation.detail.extensions.getPlayShortcutInfo
import com.swackles.jellyfin.presentation.detail.tabs.DetailScreenTabs
import org.jellyfin.sdk.model.UUID

data class DetailMediaBarLabel(
    val text: String,
    val align: TextAlign
)

data class PlayShortcutInfo(
    val progress: Float,
    val labels: List<DetailMediaBarLabel>,
    val mediaId: java.util.UUID,
    val startPosition: Long,
    val isInProgress: Boolean
)


@Destination
@Composable
fun DetailScreen(
    id: UUID,
    navigator: DestinationsNavigator,
    viewModal: DetailScreenViewModal = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val state = viewModal.getState()

    LaunchedEffect(Unit){
        viewModal.loadData(id)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.error.isNotBlank()) P(text = state.error, isError = true)
        else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        else if (state.data != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState, !state.showOverlay),
            ) {
                BannerImage(media = state.data, scrollState = scrollState)
                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                            translationY = -700f
                        }
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HeaderBox(state.data) { id, startPosition ->
                            navigator.navigate(
                                PlayerScreenDestination(
                                    id = id,
                                    startPosition = startPosition,
                                    isTvShow = state.data is MediaItem.Series)
                            )
                        }
                        DetailScreenTabs(state.data,
                            navigateToMediaView = { navigator.navigate(DetailScreenDestination(it)) },
                            activeSeason = state.activeSeason,
                            toggleOverlay = viewModal::toggleOverlay,
                            playVideo = { id, startPosition -> navigator.navigate(PlayerScreenDestination(id, startPosition)) }
                        )
                    }
                }
            }
            if (state.showOverlay && state.data is MediaItem.Series)
                Overlay(state.data.seasons(), viewModal::selectSeason, viewModal::toggleOverlay)
        }
    }
}

@Composable
private fun HeaderBox(media: MediaItem, playVideo: (id: UUID, startPosition: Long) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailScreenLoadedProps.containerPadding)
    ) {
        LogoImage(media = media)
        Spacer(modifier = Modifier.size(5.dp))
        InfoRow(media.getInfo())
        Spacer(modifier = Modifier.size(15.dp))
        PlayButtonAndProgressBar(media.getPlayShortcutInfo(), playVideo)
        Spacer(modifier = Modifier.size(15.dp))
        if (!media.overview.isNullOrBlank()) P(text = media.overview!!, modifier = Modifier.padding(horizontal = 5.dp))
    }
}

@Composable
private fun PlayButtonAndProgressBar(info: PlayShortcutInfo?, playVideo: (id: UUID, startPosition: Long) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (info != null && info.isInProgress) {
            Row(modifier = Modifier.fillMaxWidth()) {
                info.labels.map {
                    P(text = it.text, align = it.align, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                }
            }
            LinearProgressIndicator(progress = { info.progress }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(5.dp))
        }
        Button(
            shape = RoundedCornerShape(DetailScreenLoadedProps.buttonBorderRadius),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.primaryButtonColors(),
            contentPadding = ButtonDefaults.primaryButtonContentPadding(),
            enabled = info != null,
            onClick = { if (info != null) playVideo(info.mediaId, info.startPosition) }) {
            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "play arrow")
            P(text = if (info != null && info.isInProgress) "Continue Watching" else "Play")
        }
    }
}

@Composable
private fun InfoRow(labels: List<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        labels.map {
            AssistChip(
                colors = AssistChipDefaults.primaryAssistChipColors(),
                border = AssistChipDefaults.primaryAssistChipBorder(),
                onClick = { },
                label = { P(text = it) },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}

@Composable
private fun Overlay(seasons: List<Int>, selectSeason: (season: Int) -> Unit, toggleOverlay: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()
        .background(Color.Black.copy(.8f))) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                seasons.map {
                    TextButton(
                        modifier = Modifier.padding(vertical = 20.dp),
                        onClick = { selectSeason(it) },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        H2("Season $it")
                    }
                }
            }
            TextButton(
                onClick = toggleOverlay,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "cancel"
                )
            }
        }
    }
}

private object DetailScreenLoadedProps {
    val containerPadding = 20.dp
    val buttonBorderRadius = 5.dp
}

//<editor-fold desc="Preview loading">
@Composable
private fun PreviewWithLoading(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(isLoading = true)
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithLoading_Dark() {
    PreviewWithLoading(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithLoading_White() {
    PreviewWithLoading(false)
}
//</editor-fold>

//<editor-fold desc="Preview Error">
@Composable
private fun PreviewWithError(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(error = "Error")
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithError_Dark() {
    PreviewWithError(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithError_White() {
    PreviewWithError(false)
}
//</editor-fold>

//<editor-fold desc="Preview Movie">
@Composable
private fun PreviewWithMovieData(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(data = MediaItem.Movie.preview())
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMovieData_Dark() {
    PreviewWithMovieData(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMovieData_White() {
    PreviewWithMovieData(false)
}
@Composable
private fun PreviewWithMovieDataInProgress(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(data = MediaItem.Movie.preview(playedPercentage = .24f))
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMovieDataInProgress_Dark() {
    PreviewWithMovieDataInProgress(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMovieDataInProgress_White() {
    PreviewWithMovieDataInProgress(false)
}
//</editor-fold>

//<editor-fold desc="Preview Series">
@Composable
private fun PreviewWithSeriesData(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(data = MediaItem.Series.preview())
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesData_Dark() {
    PreviewWithSeriesData(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesData_White() {
    PreviewWithSeriesData(false)
}

@Composable
private fun PreviewWithSeriesDataInProgress(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(data = MediaItem.Series.preview())
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesDataInProgress_Dark() {
    PreviewWithSeriesDataInProgress(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesDataInProgress_White() {
    PreviewWithSeriesDataInProgress(false)
}

@Composable
private fun PreviewWithSeriesDataWithOverlay(isDarkTheme: Boolean) {
    val viewModal = PreviewDetailScreenViewModal(
        DetailScreenState(data = MediaItem.Series.preview(), showOverlay = true)
    )

    JellyfinTheme(isDarkTheme) {
        DetailScreen(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesDataWithOverlay_Dark() {
    PreviewWithSeriesDataWithOverlay(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithSeriesDataWithOverlay_White() {
    PreviewWithSeriesDataWithOverlay(false)
}
//</editor-fold>

