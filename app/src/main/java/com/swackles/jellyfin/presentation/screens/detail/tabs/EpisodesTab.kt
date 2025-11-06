package com.swackles.jellyfin.presentation.screens.detail.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.R
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.components.LargeTitle
import com.swackles.jellyfin.presentation.components.MediumText
import com.swackles.jellyfin.presentation.components.MediumTitle
import com.swackles.jellyfin.presentation.components.SmallTitle
import com.swackles.jellyfin.presentation.modifiers.progressStatus
import com.swackles.jellyfin.presentation.screens.detail.PlayVideo
import com.swackles.jellyfin.presentation.screens.detail.tabs.EpisodeListItemProps.EPISODE_THUMB_SIZE
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.jellyfin.presentation.styles.variantAssistChipBorder
import com.swackles.jellyfin.presentation.styles.variantAssistChipColors
import com.swackles.libs.jellyfin.Episode
import com.swackles.libs.jellyfin.getPosterUrl
import org.jellyfin.sdk.model.DateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun EpisodesTab(
    episodesBySeason: Map<Int, List<Episode>>,
    playVideo: PlayVideo
) {
    val hasMultipleSeasons = episodesBySeason.keys.size > 1
    var activeSeason by remember { mutableIntStateOf(episodesBySeason.keys.min().let { if(it < 1) 1 else it }) }
    var showOverlay by remember { mutableStateOf(false) }

    fun selectSeason(season: Int): Unit {
        activeSeason = season
        showOverlay = !showOverlay
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacings.Medium)
    ) {
        AssistChip(
            colors = AssistChipDefaults.variantAssistChipColors(),
            border = AssistChipDefaults.variantAssistChipBorder(),
            onClick = { showOverlay = !showOverlay },
            enabled = hasMultipleSeasons,
            label = {
                Text(
                    text = if (activeSeason == 0) "Specials" else "Season $activeSeason",
                    style = MaterialTheme.typography.bodyMedium
                )
                    },
            trailingIcon = { if (hasMultipleSeasons) Icon(Icons.Outlined.ExpandMore, contentDescription = "Season select caret", tint = MaterialTheme.colorScheme.onSurface) }
        )
        episodesBySeason[activeSeason]!!.map { media ->
            var modifier = if (!media.isMissing) Modifier
            else Modifier.drawWithContent {
                drawContent()
                drawRect(color = Color.Black, alpha = .3f, blendMode = BlendMode.Darken)
            }

            if (media.playedPercentage > 0) modifier = modifier.progressStatus(media.playedPercentage)

            val width = with(LocalDensity.current) { (LocalConfiguration.current.screenWidthDp * EPISODE_THUMB_SIZE).toInt() }
            val height = (width * 0.7f).toInt()

            val painter =
                if (LocalInspectionMode.current) painterResource(R.drawable.preview_episode_thumb_1)
                else rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.getPosterUrl(width, height)).size(Size.ORIGINAL).build()
                )


            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacings.Small)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painter,
                        contentDescription = "Episode thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .background(MaterialTheme.colorScheme.background)
                            .size(width.dp, height.dp)
                    )
                    Column(modifier = Modifier.fillMaxWidth()) {
                        media.getTitleStrings().mapIndexed { index, text ->                            if (index == 0) SmallTitle(modifier = Modifier.padding(start = 5.dp), text = text)
                            else MediumText(text = text)
                        }
                    }
                }
                MediumText(text = media.overview)
            }
        }
    }

    if (showOverlay)
        Overlay(episodesBySeason.keys.toList(), { selectSeason(it) }, { showOverlay = !showOverlay })
}

@Composable
private fun Overlay(seasons: List<Int>, selectSeason: (season: Int) -> Unit, toggleOverlay: () -> Unit) {
    Dialog(onDismissRequest = toggleOverlay) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.Large)
        ) {
            Column(
                modifier = Modifier.padding(Spacings.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacings.Large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LargeTitle(modifier = Modifier, text = "Select season")
                HorizontalDivider()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    seasons.map {
                        TextButton(onClick = { selectSeason(it) }) {
                            MediumTitle(text = if (it == 0) "Specials" else "Season $it")
                        }
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = toggleOverlay) {
                        MediumText(text = "Close")
                    }
                }
            }
        }
    }
}

private fun Episode.getTitleStrings(): List<String> {
    val titles = mutableListOf("$episode. $title")

    if (!isMissing) {
        var minutes = (runtimeTicks / 600000000.0).roundToInt()
        val hours = kotlin.math.floor(minutes / 60.0).toInt()
        minutes -= hours * 60

        var durationString = ""

        if (hours > 0) durationString += "$hours hours"
        if (minutes > 0) durationString += "$minutes min"

        if (durationString.isNotEmpty()) titles.add(durationString)
    }

    val today = LocalDateTime.now()

    val formatter = if (this.premiereDate > today.minusDays(7) && this.premiereDate < today.plusDays(7))
        DateTimeFormatter.ofPattern("EEEE")
    else
        DateTimeFormatter.ofPattern("d MMM y")
    titles.add("${if (this.premiereDate < LocalDateTime.now()) "Aired" else "Airs"} on ${formatter.format(premiereDate)}")


    return titles
}

private object EpisodeListItemProps {
    const val EPISODE_THUMB_SIZE = .4f
}

private fun createEpisode(
    playedPercentage: Float,
    episode: Int = 1,
    season: Int = 1,
    isMissing: Boolean = false,
    premiereDate: LocalDateTime = LocalDateTime.now(),
    durationInMinutes: Long = 24
) =
    Episode(
        id = java.util.UUID.randomUUID(),
        episode = episode,
        season = season,
        title = "Lorem Ipsum",
        playbackPositionTicks = durationInMinutes * durationInMinutes / 100,
        isMissing = false,
        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a risus enim. Nullam nulla.",
        baseUrl = "test-url",
        premiereDate = premiereDate,
        runtimeTicks = (playedPercentage * durationInMinutes).toLong(),
        playedPercentage = playedPercentage
    )

@Preview
@Composable
private fun PreviewEpisodeTab() {
    JellyfinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodesTab(
                mapOf(
                    Pair(1, listOf(
                        createEpisode(1f, 1, 1),
                        createEpisode(1f, 2, 1),
                        createEpisode(1f, 3, 1),
                        createEpisode(.44f, 4, 1),
                        createEpisode(0f, 5, 1, true, DateTime.now().minusMonths(4)),
                        createEpisode(0f, 6, 1),
                        createEpisode(0f, 7, 1, true, DateTime.now().plusMonths(4)),
                    ))
                ),
                { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEpisodeTabHasMultipleSeasons() {
    JellyfinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodesTab(
                mapOf(
                    Pair(1, listOf(
                        createEpisode(1f, 1, 1),
                        createEpisode(1f, 2, 1),
                        createEpisode(.44f, 3, 1),
                        createEpisode(0f, 4, 1),
                        createEpisode(0f, 5, 1)
                    )),
                    Pair(2, listOf(
                        createEpisode(1f, 1, 2),
                        createEpisode(1f, 2, 2),
                        createEpisode(.44f, 3, 2)
                    ))
                ),
                { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewOverlay() {
    Overlay(
        seasons = listOf(0, 1, 2, 3),
        selectSeason = {},
        toggleOverlay = {}
    )
}
