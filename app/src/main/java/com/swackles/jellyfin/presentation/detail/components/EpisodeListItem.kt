package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.domain.models.EpisodeMedia
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.progressStatus

@Composable
fun EpisodeListItem(media: EpisodeMedia) {
    Surface(
        onClick = { println("Play media") } ,
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
                    P("${media.episode}. ${media.title}")
                    P(media.getDurationString())
                }
            }
            P(media.overview)
        }
    }
}

@Composable
private fun EpisodeListItemImage(media: EpisodeMedia) {
    val size = media.getSize(LocalDensity.current, EpisodeListItemProps.size)
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(media.getImageUrl(size))
            .size(Size.ORIGINAL)
            .build(),
    )

    Image(
        painter = painter,
        contentDescription = "Movie poster",
        contentScale = ContentScale.Crop,
        modifier = Modifier
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
    val size = 20.dp
}

@Preview(showBackground = true)
@Composable
private fun PreviewEpisodeListItem() {
    EpisodeListItem(EpisodeMedia.preview())
}