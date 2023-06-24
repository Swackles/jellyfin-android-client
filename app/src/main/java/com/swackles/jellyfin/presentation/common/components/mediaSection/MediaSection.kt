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
import com.swackles.jellyfin.domain.models.Media
import com.swackles.jellyfin.domain.models.MediaSection
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
            Media(
                UUID.randomUUID(),
                50.0,
                "https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDYwXkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_FMjpg_UX1200_.jpg"
            ),
            Media(
                UUID.randomUUID(),
                20.0,
                "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UY1982_.jpg",
            ),
            Media(
                UUID.randomUUID(),
                80.0,
                "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_FMjpg_UY2048_.jpg"
            ),
            Media(
                UUID.randomUUID(),
                0.0,
                "https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UY2552_.jpg"
            ),
            Media(
                UUID.randomUUID(),
                0.0,
                "https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_FMjpg_UX974_.jpg"
            )
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