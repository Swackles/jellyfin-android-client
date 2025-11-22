package com.swackles.jellyfin.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.R
import com.swackles.jellyfin.presentation.components.WatchableCardProps.QUALITY
import com.swackles.jellyfin.presentation.modifiers.progressStatus
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.getBackDropUrl
import com.swackles.libs.jellyfin.getPosterUrl
import java.util.UUID

@Composable
fun WatchableMediaCard(
    media: LibraryItem,
    modifier: Modifier = Modifier,
    onClick: (mediaId: UUID) -> Unit
) {
    if (media is LibraryItem.Series) {
        Log.e("MediaCard", "Trying to render series, series cannot be displayed like this")

        return
    }

    val painter =
        if (LocalInspectionMode.current)
            when (media) {
                is LibraryItem.Movie -> painterResource(R.drawable.preview_thumb_1)
                is LibraryItem.Episode -> painterResource(R.drawable.preview_episode_thumb_1)
                is LibraryItem.Series -> TODO()
            }
        else rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    data =
                        when (media) {
                            is LibraryItem.Movie ->
                                media.getBackDropUrl(
                                    height = with(LocalDensity.current) { WatchableCardProps.height.toPx().toInt() },
                                    width = with(LocalDensity.current) { (WatchableCardProps.height.toPx() * WatchableCardProps.ASPECT_RATIO).toInt() },
                                    quality = QUALITY
                                )
                            is LibraryItem.Episode ->
                                media.getPosterUrl(
                                    height = with(LocalDensity.current) { WatchableCardProps.height.toPx().toInt() },
                                    width = with(LocalDensity.current) { (WatchableCardProps.height.toPx() * WatchableCardProps.ASPECT_RATIO).toInt() },
                                    quality = QUALITY
                                )
                            is LibraryItem.Series -> TODO()
                        }
                ).size(Size.ORIGINAL).build()
        )
    Card(
        modifier = modifier
            .height(WatchableCardProps.height)
            .aspectRatio(WatchableCardProps.ASPECT_RATIO)
            .clickable { onClick(media.id) }
    ) {
        Box(
            modifier = Modifier
                .progressStatus(media.playedPercentage)
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, colorScheme.background)
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacings.Small, vertical = Spacings.Large)
                ) {
                    val (col, img) = createRefs()

                    Column(
                        modifier = Modifier.constrainAs(col) {
                            start.linkTo(parent.start)
                            end.linkTo(img.start, margin = 8.dp)
                            width = Dimension.fillToConstraints
                        },
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            color = colorScheme.onBackground,
                            text = media.title(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        media.subTitle()?.let { subTitle ->
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onBackground,
                                text = subTitle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .constrainAs(img) {
                                end.linkTo(parent.end)
                                centerVerticallyTo(parent)
                            },
                        painter = painterResource(R.drawable.icon_play),
                        contentDescription = "Play Icon"
                    )
                }
            }
        }
    }
}

private fun LibraryItem.title(): String =
    when (this) {
        is LibraryItem.Movie -> this.title
        is LibraryItem.Episode -> this.seriesTitle
        else -> ""
    }

private fun LibraryItem.subTitle(): String? =
    when (this) {
        is LibraryItem.Episode -> {
            val season = if (this.season != -1) "S${this.season}" else ""
            val episode = if (this.episode != -1) "S${this.episode}" else ""

            val prefix =
                if (episode.isNotEmpty() && season.isNotEmpty()) "${season}:${episode}"
                else episode.ifEmpty { season.ifEmpty { "" } }

            "$prefix $title".trim()
        }
        else -> null
    }

private object WatchableCardProps {
    const val ASPECT_RATIO = 1.7f
    const val QUALITY = .7f
    val height = 150.dp
}

@Preview(showBackground = false)
@Composable
private fun PreviewMovieMediaCard() =
    WatchableMediaCard(LibraryItem.Movie(
        id = UUID.randomUUID(),
        title = "Title",
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) {  }

@Preview(showBackground = false)
@Composable
private fun PreviewMovieMediaCardWithProgress() =
    WatchableMediaCard(LibraryItem.Movie(
        id = UUID.randomUUID(),
        title = "Title",
        baseUrl = "",
        playedPercentage = .2f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) { }

@Preview(showBackground = false)
@Composable
private fun PreviewEpisodeMediaCard() =
    WatchableMediaCard(LibraryItem.Episode(
        id = UUID.randomUUID(),
        seriesTitle = "Title",
        title = "Episode has not started",
        episode = -1,
        season = -1,
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L
    )) {  }

@Preview(showBackground = false)
@Preview(showBackground = false)
@Composable
private fun PreviewEpisodeMediaCardWithProgress() =
    WatchableMediaCard(LibraryItem.Episode(
        id = UUID.randomUUID(),
        title = "Episode has started",
        seriesTitle = "Title",
        episode = -1,
        season = -1,
        baseUrl = "",
        playedPercentage = .2f,
        playbackPositionTicks = 0L
    )) { }