package com.swackles.jellyfin.presentation.screens.detail.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.swackles.jellyfin.R
import com.swackles.libs.jellyfin.MediaItem
import com.swackles.libs.jellyfin.getLogoUrl

@Composable
internal fun LogoImage(media: MediaItem) {
    val painter =
        if (LocalInspectionMode.current) painterResource(R.drawable.preview_logo)
        else rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(media.getLogoUrl(
                    width = LocalConfiguration.current.screenWidthDp,
                    height = 100
                )).size(Size.ORIGINAL).build()
        )

    Image(
        painter = painter,
        contentDescription = "Logo image",
        modifier = Modifier.size(LogoImageProps.width, LogoImageProps.height),
        contentScale = ContentScale.Inside
    )
}

private object LogoImageProps {
    private const val modifier: Float = 120f
    val width = (2.5 * modifier).dp
    val height = (1 * modifier).dp
}
