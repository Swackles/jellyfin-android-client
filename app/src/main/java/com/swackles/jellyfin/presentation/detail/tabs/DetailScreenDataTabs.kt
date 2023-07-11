package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.swackles.jellyfin.domain.models.DetailMedia
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
    media: DetailMedia,
    navigateToMediaView: (mediaId: UUID) -> Unit
) {
    var state by remember { mutableStateOf( if(media.isSeries) EPISODES else SIMILAR ) }
    val tabs = if (media.isMovie) DetailScreenDataTabsProps.movieTabs
    else if (media.isSeries) DetailScreenDataTabsProps.seasonTabs
    else throw RuntimeException("Unknown media type for id \"${media.id}\"")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = tabs.indexOf(state)) {
            tabs.forEachIndexed { _, title ->
                Tab(
                    text = { P(title.name) },
                    selected = state == title,
                    onClick = { state = title }
                )
            }
        }
        when(state) {
            EPISODES -> EpisodesTab(media)
            SIMILAR -> SimilarTab(media.similar, navigateToMediaView = navigateToMediaView)
            DETAILS -> DetailTab(media)
        }
    }
}

private object DetailScreenDataTabsProps {
    val movieTabs = listOf(SIMILAR, DETAILS)
    val seasonTabs = listOf(EPISODES, SIMILAR, DETAILS)
}