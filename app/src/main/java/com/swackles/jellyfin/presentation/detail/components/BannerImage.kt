package com.swackles.jellyfin.presentation.detail.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.presentation.common.components.Image

@Composable
internal fun BannerImage(media: DetailMedia, scrollState: ScrollState) {
    var width by remember { mutableStateOf(0.dp) }
    val height = width / 12 * 9
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .onGloballyPositioned {
                width = with(density) {
                    it.size.width.toDp()
                }
            }
            .graphicsLayer {
                alpha = 1f - ((scrollState.value.toFloat() / scrollState.maxValue) * 1.5f)
                translationY = 0.5f * scrollState.value
            },
        contentAlignment = Center
    ) {
        Image(
            description = "Banner image",
            width = width,
            height = height,
            url = media.getBackdropUrl(
                density = LocalDensity.current,
                height = width,
                width = height
            )
        )
    }
}
