@file:OptIn(ExperimentalLayoutApi::class)

package com.swackles.jellyfin.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.PlayerScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.presentation.components.MediaSection
import com.swackles.jellyfin.presentation.components.MediumText
import com.swackles.jellyfin.presentation.screens.detail.components.BannerImage
import com.swackles.jellyfin.presentation.screens.detail.components.LogoImage
import com.swackles.jellyfin.presentation.screens.detail.tabs.DetailScreenTabs
import com.swackles.jellyfin.presentation.screens.player.PlayerMediaItem
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.jellyfin.presentation.styles.primaryAssistChipBorder
import com.swackles.jellyfin.presentation.styles.primaryAssistChipColors
import com.swackles.jellyfin.presentation.styles.primaryButtonColors
import com.swackles.jellyfin.presentation.styles.primaryButtonContentPadding
import com.swackles.jellyfin.util.durationString
import com.swackles.libs.jellyfin.Episode
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.MediaItem
import org.jellyfin.sdk.model.UUID
import java.time.LocalDate
import java.time.LocalDateTime

typealias PlayVideo = (id: UUID, startPosition: Long) -> Unit

@Destination<RootGraph>
@Composable
fun DetailScreen(
    id: UUID,
    navigator: DestinationsNavigator,
    viewModal: DetailScreenViewModal = hiltViewModel(),
) {
    LaunchedEffect(Unit){
        viewModal.loadData(id)
    }

    DetailScreenContent(
        state = viewModal.state.value,
        onPlayVideo = { id, startPositon ->
            navigator.navigate(PlayerScreenDestination(PlayerMediaItem(id)))
        }
    )

}


@Composable
private fun DetailScreenContent(
    state: UiState,
    onPlayVideo: PlayVideo
) =
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.step) {
            is Step.Loading -> LoadingContent()
            is Step.Success -> SuccessContent(
                state = state.step,
                playVideo = onPlayVideo,
                showMediaItem = { }
            )
        }
    }

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SuccessContent(
    state: Step.Success,
    playVideo: PlayVideo,
    showMediaItem: (id: UUID) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState, /* !state.showOverlay TODO: Test */)
    ) {
        BannerImage(media = state.mediaItem, scrollState = scrollState)
        Column(
            modifier = Modifier
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    translationY = -700f
                }
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacings.Large),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacings.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacings.Medium)
                ) {
                    LogoImage(media = state.mediaItem)
                    InfoRow(state.mediaItem)
                    PlayButtonAndProgressBar(getPlayShortcutInfo(mediaItem = state.mediaItem, episodes = state.episodes), playVideo)
                    if (!state.mediaItem.overview.isNullOrBlank())
                        MediumText(text = state.mediaItem.overview!!, modifier = Modifier.padding(horizontal = 5.dp))
                }
                DetailScreenTabs(
                    media = state.mediaItem,
                    episodesBySeason = state.episodes,
                    playVideo = playVideo
                )
                MediaSection(title = "Similar", items = state.similar, onClick = showMediaItem)
            }
        }
    }
}

private data class PlayShortcutInfo(
    val progress: Float,
    val labels: List<DetailMediaBarLabel>,
    val mediaId: java.util.UUID,
    val startPosition: Long,
    val isInProgress: Boolean
)

private data class DetailMediaBarLabel(
    val text: String,
    val align: TextAlign
)

private fun getPlayShortcutInfo(mediaItem: MediaItem, episodes: Map<Int, List<Episode>>): PlayShortcutInfo =
    when(mediaItem) {
        is MediaItem.Movie -> PlayShortcutInfo(
            progress = mediaItem.playedPercentage,
            labels = listOf(DetailMediaBarLabel(durationString(mediaItem.runTimeTicks), TextAlign.Right)),
            mediaId = mediaItem.id,
            startPosition = mediaItem.playbackPositionTicks,
            isInProgress =  mediaItem.isInProgress()
        )
        is MediaItem.Series -> {
            val allEpisodes = episodes.values.flatten().filter { !it.isMissing }
            val episode = allEpisodes.find { !it.hasBeenPlayed() || !it.hasFinished() }
                ?: allEpisodes.first()

            PlayShortcutInfo(
                progress = episode.playedPercentage,
                labels = listOf(
                    DetailMediaBarLabel(
                        "S${episode.season} E${episode.episode} \"${episode.title}\"",
                        TextAlign.Left
                    ),
                    DetailMediaBarLabel(durationString(episode.runtimeTicks), TextAlign.Right),
                ),
                mediaId = episode.id,
                startPosition = episode.playbackPositionTicks,
                isInProgress = episode.isInProgress()
            )
        }
    }

@Composable
private fun PlayButtonAndProgressBar(info: PlayShortcutInfo?, playVideo: PlayVideo) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacings.Small)) {
        if (info != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                info.labels.map {
                    MediumText(text = it.text, maxLines = 1, align = it.align, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                }
            }

            if (info.isInProgress) LinearProgressIndicator(progress = { info.progress }, modifier = Modifier.fillMaxWidth())
        }
        Button(
            shape = RoundedCornerShape(DetailScreenLoadedProps.buttonBorderRadius),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.primaryButtonColors(),
            contentPadding = ButtonDefaults.primaryButtonContentPadding(),
            enabled = info != null,
            onClick = { if (info != null) playVideo(info.mediaId, info.startPosition) }
        ) {
            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "play arrow")
            MediumText(text = if (info != null && info.isInProgress) "Continue Watching" else "Play")
        }
    }
}

@Composable
private fun InfoRow(media: MediaItem) =
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        val labels = when(media) {
            is MediaItem.Movie -> listOf(
                media.premiereDate.year.toString(),
                // durationString(runTimeTicks), TODO: Fix
                media.genres.first()
            )
            is MediaItem.Series -> listOf(
                media.premiereDate.year.toString(),
                media.genres.first()
            )
        }

        labels.map {
            AssistChip(
                colors = AssistChipDefaults.primaryAssistChipColors(),
                border = AssistChipDefaults.primaryAssistChipBorder(),
                onClick = { },
                label = { MediumText(text = it) },
                modifier = Modifier.padding(horizontal = Spacings.Small)
            )
        }
    }

private object DetailScreenLoadedProps {
    val buttonBorderRadius = 5.dp
}

@Preview
@Composable
private fun PreviewWithLoading() {
    JellyfinTheme {
        DetailScreenContent(
            state = UiState(Step.Loading),
            onPlayVideo = { id, startPosition -> }
        )
    }
}


private fun createEpisode(
    playedPercentage: Float,
    episode: Int = 1,
    season: Int = 1,
    premiereDate: LocalDateTime = LocalDateTime.now(),
    durationInMinutes: Long = 24
) =
    Episode(
        id = java.util.UUID.randomUUID(),
        episode = episode,
        season = season,
        title = "Lorem Ipsum",
        playbackPositionTicks = (playedPercentage * durationInMinutes * 600000000).toLong(),
        isMissing = false,
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a risus enim. Nullam nulla.",
        baseUrl = "test-url",
        premiereDate = premiereDate,
        runtimeTicks = durationInMinutes * 600000000,
        playedPercentage = playedPercentage
    )

@Preview
@Composable
private fun PreviewWithMovieData() {
    val previewLibraryItem = LibraryItem.Movie(
        id = java.util.UUID.randomUUID(),
        title = "Title",
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )

    val similar = listOf(
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem
    )

    JellyfinTheme {
        DetailScreenContent(
            state = UiState(step = Step.Success(
                mediaItem = MediaItem.Movie(
                    id = java.util.UUID.randomUUID(),
                    overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
                    genres = listOf("Action", "Adventure", "Science Fiction", "Thriller"),
                    rating = "PG-13",
                    people = emptyList(),
                    baseUrl = "test-url",
                    premiereDate = LocalDate.now(),
                    runTimeTicks = 24 * 600000000,
                    playbackPositionTicks = (200 * .5f).toLong(),
                    playedPercentage = .5f,
                    ),
                similar = similar,
                episodes = mapOf()
            )),
            onPlayVideo = { id, startPosition -> }
        )
    }
}

/*
@Preview
@Composable
private fun PreviewWithMovieDataInProgress() {
    JellyfinTheme {
        DetailScreenContent(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}
*/

@Preview
@Composable
private fun PreviewWithSeriesData() {
    val previewLibraryItem = LibraryItem.Movie(
        id = java.util.UUID.randomUUID(),
        title = "Title",
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )

    val similar = listOf(
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem,
        previewLibraryItem, previewLibraryItem, previewLibraryItem
    )

    JellyfinTheme {
        DetailScreenContent(
            state = UiState(step = Step.Success(
                mediaItem = MediaItem.Series(
                    id = java.util.UUID.randomUUID(),
                    overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus congue id lectus vitae efficitur. In nec sem quis mauris sodales interdum id ut nisl.",
                    genres = listOf("Action", "Adventure", "Science Fiction", "Thriller"),
                    rating = "PG-13",
                    people = emptyList(),
                    baseUrl = "test-url",
                    premiereDate = LocalDate.now(),
                    runTimeTicks = 24 * 600000000
                ),
                similar = similar,
                episodes = mapOf(
                    Pair(1, listOf(
                        createEpisode(1f, 1, 1),
                        createEpisode(.5f, 2, 1),
                        createEpisode(.44f, 3, 1),
                        createEpisode(0f, 4, 1),
                        createEpisode(0f, 5, 1)
                    )),
                    Pair(2, listOf(
                        createEpisode(1f, 1, 2),
                        createEpisode(1f, 2, 2),
                        createEpisode(.44f, 3, 2)
                    ))
                )
            )),
            onPlayVideo = { id, startPosition -> }
        )
    }
}

/*
@Preview
@Composable
private fun PreviewWithSeriesDataInProgress() {
    JellyfinTheme {
        DetailScreenContent(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}

@Preview
@Composable
private fun PreviewWithSeriesDataWithOverlay() {
    JellyfinTheme {
        DetailScreenContent(UUID.randomUUID(), EmptyDestinationsNavigator, viewModal)
    }
}
 */
