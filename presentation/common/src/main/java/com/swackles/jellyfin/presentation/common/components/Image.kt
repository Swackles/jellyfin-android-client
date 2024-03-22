package com.swackles.jellyfin.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.presentation.common.R


@Composable
fun Image(
    url: String,
    modifier: Modifier = Modifier,
    description: String,
    scale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha
) {

    val painter  = if (LocalInspectionMode.current) painterResource(id = url.toInt())
    else {
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(url)
                .size(Size.ORIGINAL)
                .build(),
        )
    }

    androidx.compose.foundation.Image(
        painter = painter,
        contentDescription = description,
        contentScale = scale,
        modifier = modifier,
        alpha = alpha,
    )
}

@Preview
@Composable
private fun PreviewImage() {
    Image(R.drawable.movie_logo_image.toString(), Modifier, "desc")
}