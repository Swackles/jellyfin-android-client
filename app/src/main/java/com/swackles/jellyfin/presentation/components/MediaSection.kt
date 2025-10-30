package com.swackles.jellyfin.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.LibraryItem
import java.util.UUID

@Composable
fun MediaSection(
    title: String,
    items: List<LibraryItem>,
    onClick: (mediaId: UUID) -> Unit
) {
    if (items.isEmpty()) return

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacings.Small)) {
            Text(
                modifier = Modifier.padding(horizontal = Spacings.Medium),
                style = MaterialTheme.typography.titleMedium,
                text = title
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacings.Medium),
                state = rememberLazyListState()
            ) {
                itemsIndexed(items) { pos, media ->
                    MediaCard(
                        modifier = if (pos == 0) Modifier.padding(start = Spacings.Medium)
                        else if (pos == items.size - 1) Modifier.padding(end = Spacings.Medium)
                        else Modifier,
                        media = media,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediaSection() {
    val previewLibraryItem = LibraryItem.Movie(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )

    MediaSection(
        title = "Media section preview",
        items = listOf(previewLibraryItem, previewLibraryItem, previewLibraryItem, previewLibraryItem, previewLibraryItem)
    ) { }
}

@Preview()
@Composable
private fun PreviewEmptyMediaSection() {
    MediaSection(
        title = "Media section preview",
        items = emptyList()
    ) { }
}