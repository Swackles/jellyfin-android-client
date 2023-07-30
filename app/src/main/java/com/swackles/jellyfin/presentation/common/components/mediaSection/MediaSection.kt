package com.swackles.jellyfin.presentation.common.components.mediaSection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.common.components.H2
import com.swackles.jellyfin.data.models.Media
import com.swackles.jellyfin.data.models.MediaSection
import java.util.UUID

@Composable
fun MediaSection(section: MediaSection, onClick: (mediaId: UUID) -> Unit) {
    if (section.isEmpty())
        Box( modifier = Modifier.size(0.dp))
    else {
        Box(modifier = Modifier.fillMaxWidth()) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(top = MediaSectionProps.paddingTop)
                ) {
                    H2(text = section.title)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        state = rememberLazyListState()
                    ) {
                        itemsIndexed(section.medias) { _, media ->
                            MediaCard(media, onClick)
                        }
                    }

                }
            }
        }
    }
}

private object MediaSectionProps {
    val paddingTop = 10.dp
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaSection() {
    val section = MediaSection(
        title = "Media section preview",
        medias = listOf(
            Media.preview(),
            Media.preview(),
            Media.preview(),
            Media.preview(),
            Media.preview(),
        )
    )

    MediaSection(section = section) { id -> println(id) }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmptyMediaSection() {
    val section = MediaSection(
        title = "Media section preview",
        medias = emptyList()
    )

    MediaSection(section = section) { id -> println(id) }
}