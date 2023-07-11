package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.presentation.common.components.Image

@Composable
internal fun LogoImage(media: DetailMedia) {
    Image(
        description = "Logo image",
        width = LogoImageProps.width,
        height = LogoImageProps.height,
        scale = ContentScale.Inside,
        url = media.getLogoUrl(
            density = LocalDensity.current,
            height = LogoImageProps.height,
            width = LogoImageProps.width
        )
    )
}

private object LogoImageProps {
    private const val modifier: Float = 120f
    val width = (2.5 * modifier).dp
    val height = (1 * modifier).dp
}