package com.swackles.jellyfin.presentation.screens.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.swackles.jellyfin.R
import com.swackles.libs.jellyfin.MediaItem
import com.swackles.libs.jellyfin.getBackDropUrl

@Composable
internal fun BannerImage(media: MediaItem, scrollState: ScrollState) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val height = width * 1.7f

    val offsetY = 0.5f * scrollState.value
    val brush = Brush.verticalGradient(
        0.2f to Color.Transparent,
        1f to MaterialTheme.colorScheme.background
    )

    val imageSize: Size = with(LocalDensity.current) {
        Size(width = width.toPx(), height = height.toPx())
    }

    val painter =
        if (LocalInspectionMode.current) painterResource(R.drawable.preview_background)
        else rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(media.getBackDropUrl(imageSize.width.toInt(), imageSize.height.toInt()))
                .size(coil.size.Size.ORIGINAL).build()
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(alpha = .5f))
            .height(300.dp),
        contentAlignment = TopCenter
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "Banner image",
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        size = Size(width.toPx() * 1.1f, height.toPx() * 1.7f),
                        topLeft = Offset(0f, offsetY)
                    )
                }
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    alpha = 1f - scrollState.value.toFloat() / height.value * .7f
                    translationY = offsetY
                }
        )
    }
}
