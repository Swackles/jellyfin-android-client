package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Top
import com.swackles.jellyfin.domain.models.Media
import com.swackles.jellyfin.presentation.common.components.mediaSection.MediaCard
import java.util.UUID


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimilarTab(medias: List<Media>, navigateToMediaView: (mediaId: UUID) -> Unit) {
    FlowRow(
        horizontalArrangement = Center,
        verticalAlignment = Top
    ) {
        medias.map { MediaCard(media = it, onClick = navigateToMediaView) }
    }
}
