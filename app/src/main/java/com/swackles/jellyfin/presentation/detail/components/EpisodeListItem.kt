package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.jellyfin.models.LibraryItem
import com.swackles.jellyfin.presentation.common.components.Image
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.extensions.getAiredString
import com.swackles.jellyfin.presentation.common.extensions.getBackDropImage
import com.swackles.jellyfin.presentation.common.extensions.getDurationString
import com.swackles.jellyfin.presentation.common.preview.missingPreview
import com.swackles.jellyfin.presentation.common.preview.preview
import com.swackles.jellyfin.presentation.common.progressStatus
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.UUID

@Composable
fun EpisodeListItem(media: LibraryItem.Episode, playVideo: (id: UUID, startPosition: Long) -> Unit) {
    Surface(
        onClick = { playVideo(media.id, media.playbackPositionTicks) } ,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(
            start = 0.dp,
            top = EpisodeListItemProps.paddingTop,
            end = 0.dp,
            bottom = EpisodeListItemProps.paddingBottom
        )) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                EpisodeListItemImage(media = media)
                Column(modifier = Modifier.fillMaxWidth()) {
                    P(text = media.title ?: "Unknown Title")
                    P(text =
                        if (media.isMissing) media.getAiredString()
                        else media.getDurationString()
                    )
                }
            }
            P(text = media.overview ?: "")
        }
    }
}

@Composable
private fun EpisodeListItemImage(media: LibraryItem.Episode) {
    val modifier = if (!media.isMissing) Modifier
        else Modifier.drawWithContent {
            drawContent()
            drawRect(color = Color.Black, alpha = .3f, blendMode = BlendMode.Darken)
        }

    val width = EpisodeListItemProps.size * 1.8f
    val height = EpisodeListItemProps.size

    Image(
        url = media.getBackDropImage(LocalDensity.current, width, height),
        description = "Episode thumbnail",
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .size(width, height)
            .then(
                if (media.isInProgress) Modifier.progressStatus(
                    media.progress,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onSurfaceVariant
                ) else Modifier
            )
    )
}

private object EpisodeListItemProps {
    val paddingTop = 10.dp
    val paddingBottom = paddingTop * 2f
    val size = 60.dp
}

@Composable
private fun PreviewEpisodeListItem(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodeListItem(LibraryItem.Episode.preview(0f, 1, 1)) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItem_dark() {
    PreviewEpisodeListItem(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItem_white() {
    PreviewEpisodeListItem(false)
}

@Composable
private fun PreviewEpisodeListItemWithProgress(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodeListItem(LibraryItem.Episode.preview(.44f, 1, 1)) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemWithProgress_dark() {
    PreviewEpisodeListItemWithProgress(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemWithProgress_white() {
    PreviewEpisodeListItemWithProgress(false)
}

@Composable
private fun PreviewEpisodeListItemMissingAired(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodeListItem(LibraryItem.Episode.missingPreview(1, 1, DateTime.now().minusMonths(4))) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemMissingAired_dark() {
    PreviewEpisodeListItemMissingAired(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemMissingAired_white() {
    PreviewEpisodeListItemMissingAired(false)
}

@Composable
private fun PreviewEpisodeListItemMissingComingSoon(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EpisodeListItem(LibraryItem.Episode.missingPreview(1, 1, DateTime.now().plusMonths(4))) { _, _ -> }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemMissingComingSoon_dark() {
    PreviewEpisodeListItemMissingComingSoon(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItemMissingComingSoon_white() {
    PreviewEpisodeListItemMissingComingSoon(false)
}