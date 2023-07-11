package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun Image(
    url: String,
    width: Dp,
    height: Dp,
    description: String,
    scale: ContentScale = ContentScale.Crop
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(Size.ORIGINAL)
            .build(),
    )

    androidx.compose.foundation.Image(
        painter = painter,
        contentDescription = description,
        contentScale = scale,
        modifier = Modifier
            .height(height)
            .width(width)
    )
}
