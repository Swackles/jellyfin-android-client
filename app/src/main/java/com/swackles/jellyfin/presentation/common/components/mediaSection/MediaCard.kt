package com.swackles.jellyfin.presentation.common.components.mediaSection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.swackles.jellyfin.domain.models.Media
import java.util.UUID

@Composable
fun MediaCard(media: Media, onClick: (mediaId: UUID) -> Unit) {
    Card(
        modifier = Modifier
            .padding(CardProps.padding)
            .height(CardProps.height)
            .width(CardProps.width)
            .clickable { onClick(media.id) }
    ) {
        CardImage(media)
    }
}

@Composable
private fun CardImage(media: Media) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(media.getPosterUrl(
                density = LocalDensity.current,
                height = CardProps.height,
                width = CardProps.width
            ))
            .size(coil.size.Size.ORIGINAL)
            .build(),
    )

    Image(
        painter = painter,
        contentDescription = "Movie poster",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
            .then(if(media.isInProgress) Modifier.progressStatus(media.progress, colorScheme.primary, colorScheme.onSurfaceVariant) else Modifier)
    )
}

private fun Modifier.progressStatus(progress: Float, colorOne: Color, colorTwo: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { 5.dp.toPx() }

        Modifier.drawWithContent {
            drawContent()

            val width = size.width * progress
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = colorOne,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )

            drawLine(
                color = colorTwo.copy(.5f),
                start = Offset(x = width, y = height),
                end = Offset(x = size.width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

private object CardProps {
    private const val modifier: Float = 55f
    val padding = 3.dp
    val width = (2 * modifier).dp
    val height = (3 * modifier).dp
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaCard() {
    val media = Media(
        UUID.randomUUID(),
        0.0,
        "https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_FMjpg_UX974_.jpg"
    )

    MediaCard(media) { id -> println(id) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaCardWithProgress() {
    val media = Media(
        UUID.randomUUID(),
        20.0,
        "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UY1982_.jpg",
    )

    MediaCard(media) { id -> println(id) }
}