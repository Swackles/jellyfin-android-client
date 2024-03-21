package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import com.swackles.jellyfin.data.jellyfin.models.Media
import com.swackles.jellyfin.presentation.common.components.mediaSection.MediaCard
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimilarTab(medias: List<Media>, navigateToMediaView: (mediaId: UUID) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        medias.map { MediaCard(media = it, onClick = navigateToMediaView) }
    }
}
