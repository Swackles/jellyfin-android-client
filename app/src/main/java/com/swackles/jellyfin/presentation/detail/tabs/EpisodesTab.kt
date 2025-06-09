package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.presentation.common.colors.variantAssistChipBorder
import com.swackles.jellyfin.presentation.common.colors.variantAssistChipColors
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.preview.missingPreview
import com.swackles.jellyfin.presentation.common.preview.preview
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.presentation.detail.components.EpisodeListItem
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.UUID

@Composable
fun EpisodesTab(
    episodesBySeason: Map<Int, List<LibraryItem.Episode>>,
    activeSeason: Int,
    toggleOverlay: () -> Unit,
    playVideo: (id: UUID, startPosition: Long) -> Unit
) {
    val hasMultipleSeasons = episodesBySeason.keys.size > 1

    Column(modifier = Modifier.fillMaxWidth()) {
        AssistChip(
            colors = AssistChipDefaults.variantAssistChipColors(),
            border = AssistChipDefaults.variantAssistChipBorder(),
            onClick = toggleOverlay,
            enabled = hasMultipleSeasons,
            label = { P(text = "Season $activeSeason") },
            trailingIcon = { if (hasMultipleSeasons) Icon(Icons.Outlined.ExpandMore, contentDescription = "Season select caret", tint = MaterialTheme.colorScheme.onSurface) }
        )
        episodesBySeason[activeSeason]!!.map {
            EpisodeListItem(media = it, playVideo)
        }
    }
}

@Composable
private fun PreviewEpisodeTab(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodesTab(
                mapOf(
                    Pair(1, listOf(
                        LibraryItem.Episode.preview(1f, 1, 1),
                        LibraryItem.Episode.preview(1f, 1, 1),
                        LibraryItem.Episode.preview(.44f, 3, 1),
                        LibraryItem.Episode.missingPreview(3, 1, DateTime.now().minusMonths(4)),
                        LibraryItem.Episode.preview(0f, 5, 1),
                        LibraryItem.Episode.missingPreview(3, 1, DateTime.now().plusMonths(4)),
                    ))
                ), 1, {}
            ) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeTab_dark() {
    PreviewEpisodeTab(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeTab_white() {
    PreviewEpisodeTab(false)
}

@Composable
private fun PreviewEpisodeTabHasMultipleSeasons(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodesTab(
                mapOf(
                    Pair(1, listOf(
                        LibraryItem.Episode.preview(1f, 1, 1),
                        LibraryItem.Episode.preview(1f, 1, 1),
                        LibraryItem.Episode.preview(.44f, 3, 1),
                        LibraryItem.Episode.preview(0f, 4, 1),
                        LibraryItem.Episode.preview(0f, 5, 1)
                    )),
                    Pair(2, listOf(
                        LibraryItem.Episode.preview(1f, 1, 2),
                        LibraryItem.Episode.preview(1f, 1, 2),
                        LibraryItem.Episode.preview(.44f, 3, 2)
                    ))
                ), 1, {}
            ) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeTabHasMultipleSeasons_dark() {
    PreviewEpisodeTabHasMultipleSeasons(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeTabHasMultipleSeasons_white() {
    PreviewEpisodeTabHasMultipleSeasons(false)
}