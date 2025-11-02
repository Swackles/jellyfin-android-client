package com.swackles.jellyfin.presentation.components

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
import com.swackles.jellyfin.presentation.modifiers.progressStatus
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.getPosterUrl
import com.swackles.libs.jellyfin.getThumbUrl
import java.util.UUID

@Composable
fun MediaCard(
    media: LibraryItem,
    modifier: Modifier = Modifier,
    onClick: (mediaId: UUID) -> Unit
) {
    val aspectRatio = when(media) {
        is LibraryItem.Movie -> if (media.playedPercentage != 0f) CardProps.thumbAspectRatio else CardProps.posterAspectRatio
        is LibraryItem.Series -> CardProps.posterAspectRatio
        is LibraryItem.Episode -> CardProps.thumbAspectRatio
    }

    Card(
        modifier = modifier
            .height(CardProps.height)
            .aspectRatio(aspectRatio)
            .clickable { onClick(media.id) }
    ) {
        when(media) {
            is LibraryItem.Movie -> MovieCardContent(media, CardProps.posterAspectRatio)
            is LibraryItem.Series -> SeriesCardContent(media, CardProps.posterAspectRatio)
            is LibraryItem.Episode -> EpisodeCardContent(media, CardProps.thumbAspectRatio)
        }
    }
}

@Composable
private fun EpisodeCardContent(media: LibraryItem.Episode, aspectRatio: Float) {
    val painter =
        if (LocalInspectionMode.current) painterResource(R.drawable.preview_episode_thumb_1)
        else rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(media.getPosterUrl(
                    height = with(LocalDensity.current) { CardProps.height.toPx().toInt() },
                    width = with(LocalDensity.current) { (CardProps.height.toPx() * aspectRatio).toInt() }
                )).size(Size.ORIGINAL).build()
        )

    val modifier =
        if (media.playedPercentage != 0f) Modifier.progressStatus(media.playedPercentage)
        else Modifier

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Episode thumbnail",
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = modifier.fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(Color.Transparent, colorScheme.background)
                )),
            contentAlignment = Alignment.BottomStart
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
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
                    if (media.seriesTitle.isNotEmpty()) Text(
                        style = MaterialTheme.typography.titleSmall,
                        color = colorScheme.onBackground,
                        text = media.seriesTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (media.episodeString().isNotEmpty()) Text(
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onBackground,
                        text = media.episodeString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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

private fun LibraryItem.Episode.episodeString(): String {
    val season = if (this.season != -1) "S${this.season}" else ""
    val episode = if (this.episode != -1) "S${this.episode}" else ""

    val prefix =
        if (episode.isNotEmpty() && season.isNotEmpty()) "${season}:${episode}"
        else episode.ifEmpty { season.ifEmpty { "" } }

    return "$prefix $title".trim()
}

@Composable
private fun MovieCardContent(media: LibraryItem.Movie, aspectRatio: Float) {
    val url = if (media.playedPercentage != 0f) media.getThumbUrl(
        height = with(LocalDensity.current) { CardProps.height.toPx().toInt() },
        width = with(LocalDensity.current) { (CardProps.height.toPx() * aspectRatio).toInt() }
    ) else media.getPosterUrl(
        height = with(LocalDensity.current) { CardProps.height.toPx().toInt() },
        width = with(LocalDensity.current) { (CardProps.height.toPx() * aspectRatio).toInt() }
    )

    val painter =
        if (LocalInspectionMode.current) {
            if (media.playedPercentage != 0f) painterResource(R.drawable.preview_thumb_1)
            else painterResource(R.drawable.preview_poster_1)
        }
        else rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url).size(Size.ORIGINAL).build()
        )

    val modifier =
        if (media.playedPercentage != 0f) Modifier.progressStatus(media.playedPercentage)
        else Modifier

    Image(
        painter = painter,
        contentDescription = "Movie poster",
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun SeriesCardContent(media: LibraryItem.Series, aspectRatio: Float) =
    Image(
        painter =
            if (LocalInspectionMode.current) painterResource(R.drawable.preview_poster_1)
            else rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(media.getPosterUrl(
                        height = with(LocalDensity.current) { CardProps.height.toPx().toInt() },
                        width = with(LocalDensity.current) { (CardProps.height.toPx() * aspectRatio).toInt() }
                    )).size(Size.ORIGINAL).build()
            ),
        contentDescription = "series poster",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

private object CardProps {
    const val posterAspectRatio = .7f
    const val thumbAspectRatio = 1.7f
    val height = 150.dp
}

@Preview(showBackground = false)
@Composable
private fun PreviewMovieMediaCard() =
    MediaCard(LibraryItem.Movie(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) {  }

@Preview(showBackground = false)
@Composable
private fun PreviewMovieMediaCardWithProgress() =
    MediaCard(LibraryItem.Movie(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 20f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) { }

@Preview(showBackground = false)
@Composable
private fun PreviewSeriesMediaCard() =
    MediaCard(LibraryItem.Series(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) {  }

@Preview(showBackground = false)
@Composable
private fun PreviewSeriesMediaCardWithProgress() =
    MediaCard(LibraryItem.Series(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 20f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) { }

@Preview(showBackground = false)
@Composable
private fun PreviewEpisodeMediaCard() =
    MediaCard(LibraryItem.Episode(
        id = UUID.randomUUID(),
        seriesTitle = "Oppenheimer",
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
    MediaCard(LibraryItem.Episode(
        id = UUID.randomUUID(),
        title = "Episode has started",
        seriesTitle = "Oppenheimer",
        episode = -1,
        season = -1,
        baseUrl = "",
        playedPercentage = 20f,
        playbackPositionTicks = 0L
    )) { }