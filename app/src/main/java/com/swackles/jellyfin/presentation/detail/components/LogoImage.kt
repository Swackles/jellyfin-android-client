package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.presentation.common.components.Image
import com.swackles.jellyfin.presentation.common.extensions.getLogoUrl

@Composable
internal fun LogoImage(media: MediaItem) {
    Image(
        description = "Logo image",
        modifier = Modifier.size(LogoImageProps.width, LogoImageProps.height),
        scale = ContentScale.Inside,
        url = media.getLogoUrl(
            density = LocalDensity.current,
            width = LocalConfiguration.current.screenWidthDp.dp
        )
    )
}

private object LogoImageProps {
    private const val modifier: Float = 120f
    val width = (2.5 * modifier).dp
    val height = (1 * modifier).dp
}