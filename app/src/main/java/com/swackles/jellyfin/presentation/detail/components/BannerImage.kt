package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.domain.models.DetailMediaBase
import com.swackles.jellyfin.domain.models.DetailMediaMoviePreview
import com.swackles.jellyfin.presentation.common.components.Image
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Composable
internal fun BannerImage(media: DetailMediaBase, scrollState: ScrollState) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val height = media.getPosterImageHeight(width)
    val offsetY = 0.5f * scrollState.value
    val brush = Brush.verticalGradient(0.2f to Color.Transparent, 1f to MaterialTheme.colorScheme.background)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = TopCenter
    ) {
        Image(
            scale = ContentScale.Crop,
            description = "Banner image",
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(brush = brush, size = Size(width.toPx() * 1.1f, height.toPx() * 1.7f), topLeft = Offset(0f, offsetY))
                }
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    alpha = 1f - scrollState.value.toFloat() / height.value * .7f
                    translationY = offsetY
                },
            url = media.getBackdropUrl(
                density = LocalDensity.current,
                width = width
            )
        )
    }
}

@Composable
private fun PreviewBannerImage(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BannerImage(media = DetailMediaMoviePreview(), scrollState = rememberScrollState())
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBannerImage_Dark() {
    PreviewBannerImage(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewBannerImage_White() {
    PreviewBannerImage(false)
}