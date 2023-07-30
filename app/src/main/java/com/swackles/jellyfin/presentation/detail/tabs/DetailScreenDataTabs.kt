package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.domain.models.DetailMediaBase
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.detail.tabs.Tabs.DETAILS
import com.swackles.jellyfin.presentation.detail.tabs.Tabs.EPISODES
import com.swackles.jellyfin.presentation.detail.tabs.Tabs.SIMILAR
import java.util.UUID

private enum class Tabs {
    SIMILAR,
    DETAILS,
    EPISODES
}

@Composable
fun DetailScreenTabs(
    media: DetailMediaBase,
    navigateToMediaView: (mediaId: UUID) -> Unit,
    toggleOverlay: () -> Unit,
    playVideo: (id: UUID, startPosition: Long) -> Unit,
    activeSeason: Int
) {
    var state by remember { mutableStateOf( if(media.isSeries) EPISODES else SIMILAR ) }
    val tabs = if (media.isMovie) DetailScreenDataTabsProps.movieTabs
    else if (media.isSeries) DetailScreenDataTabsProps.seasonTabs
    else throw RuntimeException("Unknown media type for id \"${media.id}\"")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = tabs.indexOf(state), modifier = Modifier.padding(top = 10.dp)) {
            tabs.forEachIndexed { _, title ->
                Tab(
                    text = { P(text = title.name) },
                    selected = state == title,
                    onClick = { state = title }
                )
            }
        }
        Box(modifier = Modifier.padding(20.dp)) {
            when(state) {
                EPISODES -> EpisodesTab(media.getEpisodes(), activeSeason, toggleOverlay, playVideo)
                SIMILAR -> SimilarTab(media.similar, navigateToMediaView = navigateToMediaView)
                DETAILS -> DetailTab(media)
            }
        }
    }
}

private object DetailScreenDataTabsProps {
    val movieTabs = listOf(SIMILAR, DETAILS)
    val seasonTabs = listOf(EPISODES, SIMILAR, DETAILS)
}