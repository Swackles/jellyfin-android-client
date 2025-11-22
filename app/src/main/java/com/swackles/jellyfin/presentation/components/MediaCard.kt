package com.swackles.jellyfin.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.R
import com.swackles.jellyfin.presentation.components.CardProps.QUALITY
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.getPosterUrl
import java.util.UUID

@Composable
fun MediaCard(
    media: LibraryItem,
    modifier: Modifier = Modifier,
    onClick: (mediaId: UUID) -> Unit
) {
    if (media is LibraryItem.Episode) {
        Log.e("MediaCard", "Trying to render episode, episode cannot be displayed like this")

        return
    }

    Card(
        modifier = modifier
            .height(CardProps.height)
            .aspectRatio(CardProps.ASPECT_RATIO)
            .clickable { onClick(media.id) }
    ) {
        Image(
            painter =
                if (LocalInspectionMode.current) painterResource(R.drawable.preview_poster_1)
                else rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.getPosterUrl(
                            height = with(LocalDensity.current) { CardProps.height.toPx().toInt() },
                            width = with(LocalDensity.current) { (CardProps.height.toPx() * CardProps.ASPECT_RATIO).toInt() },
                            quality = QUALITY
                        )).size(Size.ORIGINAL).build()
                ),
            contentDescription = "Movie poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private object CardProps {
    const val ASPECT_RATIO = .7f
    const val QUALITY = .7f
    val height = 150.dp
}

@Preview(showBackground = false)
@Composable
private fun PreviewMovieMediaCard() =
    MediaCard(LibraryItem.Movie(
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
    MediaCard(LibraryItem.Movie(
        id = UUID.randomUUID(),
        title = "Title",
        baseUrl = "",
        playedPercentage = .2f,
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
        playedPercentage = .2f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )) { }