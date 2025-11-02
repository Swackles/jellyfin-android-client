package com.swackles.jellyfin.presentation.screens.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.swackles.jellyfin.presentation.components.MediumText
import com.swackles.jellyfin.presentation.components.MediumTextMuted
import com.swackles.jellyfin.presentation.screens.detail.PlayVideo
import com.swackles.jellyfin.presentation.screens.detail.tabs.Tabs.DETAILS
import com.swackles.jellyfin.presentation.screens.detail.tabs.Tabs.EPISODES
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.Episode
import com.swackles.libs.jellyfin.MediaItem

private enum class Tabs {
    DETAILS,
    EPISODES
}

@Composable
fun DetailScreenTabs(
    media: MediaItem,
    episodesBySeason: Map<Int, List<Episode>>,
    playVideo: PlayVideo
) =
    when(media) {
        is MediaItem.Movie -> MovieDetailScreenTabs(data = media.getDetails())
        is MediaItem.Series -> SeriesDetailScreenTabs(
            media = media,
            episodesBySeason = episodesBySeason,
            playVideo = playVideo
        )
    }

@Composable
private fun SeriesDetailScreenTabs(
    media: MediaItem,
    episodesBySeason: Map<Int, List<Episode>>,
    playVideo: PlayVideo
) {
    var state by remember { mutableStateOf( if(media is MediaItem.Series) EPISODES else DETAILS ) }
    val tabs = when (media) {
        is MediaItem.Movie -> listOf(DETAILS)
        is MediaItem.Series -> listOf(EPISODES, DETAILS)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = tabs.indexOf(state), modifier = Modifier.padding(top = Spacings.Medium)) {
            tabs.forEachIndexed { _, title ->
                Tab(
                    text = { MediumText(text = title.name) },
                    selected = state == title,
                    onClick = { state = title }
                )
            }
        }
        Box(modifier = Modifier.padding(Spacings.Large)) {
            when(state) {
                EPISODES -> if (media is MediaItem.Series) EpisodesTab(episodesBySeason, playVideo)
                DETAILS -> DetailSectionTab(data = media.getDetails())
            }
        }
    }
}

@Composable
private fun MovieDetailScreenTabs(data: DetailSectionData) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacings.Medium), verticalArrangement = Arrangement.spacedBy(Spacings.Small)) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "Details"
        )
        DetailSectionTab(data)
    }

}

typealias DetailSectionData = Map<String, String>

@Composable
private fun DetailSectionTab(data: DetailSectionData) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacings.Small)) {
        data.map {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacings.Small)
            ) {
                MediumText(text = it.key)
                MediumTextMuted(text = it.value)
            }
        }
    }
}

private fun MediaItem.getDetails(): DetailSectionData {
    val details = mutableMapOf<String, String>()

    if (genres.isNotEmpty()) details["Genres"] = genres.joinToString(", ")
    if (rating != null) details["Rating"] = rating!!
    if (getDirectors().isNotEmpty()) details["Directors"] = getDirectors().joinToString(", ")
    if (getProducers().isNotEmpty()) details["Producers"] = getProducers().joinToString(", ")
    if (getWriters().isNotEmpty()) details["Writers"] = getWriters().joinToString(", ")
    if (getActors().isNotEmpty()) details["Actors"] = getActors().joinToString(", ")

    return details
}
