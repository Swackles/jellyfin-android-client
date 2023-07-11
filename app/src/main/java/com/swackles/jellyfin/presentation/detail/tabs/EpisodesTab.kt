package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.presentation.common.components.H2
import com.swackles.jellyfin.presentation.detail.components.EpisodeListItem


@Composable
fun EpisodesTab(media: DetailMedia) {
    Column(modifier = Modifier.fillMaxWidth()) {
        media.getEpisodes.map {
            H2("Season ${it.key}")
            Divider()
            it.value.map {media ->
                EpisodeListItem(media = media)
            }
        }
    }
}

