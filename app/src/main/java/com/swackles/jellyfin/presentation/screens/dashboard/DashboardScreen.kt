@file:OptIn(ExperimentalLayoutApi::class)

package com.swackles.jellyfin.presentation.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.R
import com.swackles.jellyfin.presentation.components.MediaSection
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.LibraryItem
import com.swackles.libs.jellyfin.getBackDropUrl
import java.util.UUID

@Destination
@Composable
fun DashboardScreen(
    viewModal: DashboardViewModal = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state = viewModal.state.value

    DashboardScreenContent(
        state = state,
        onClickDetailView = {  },
        onClickGenre = {  }
    )
}


@Composable
fun DashboardScreenContent(
    state: UiState,
    onClickDetailView: (id: UUID) -> Unit,
    onClickGenre: (genre: String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.step) {
            is Step.Loading -> LoadingContent()
            is Step.Success -> DataContent(
                sections = state.step.sections,
                onClickDetailView = onClickDetailView,
                onClickGenre = onClickGenre
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun DataContent(
    sections: List<UiSection>,
    onClickDetailView: (id: UUID) -> Unit,
    onClickGenre: (genre: String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacings.Large)) {
        itemsIndexed(sections) { _, section ->
            when (section) {
                is UiSection.Carousel -> MediaSection(
                    title = section.title,
                    items = section.items,
                    onClick = onClickDetailView
                )
                is UiSection.ButtonCards -> {
                    FlowRow(
                        modifier = Modifier.padding(Spacings.Medium),
                        horizontalArrangement = Arrangement.spacedBy(Spacings.Medium),
                        verticalArrangement = Arrangement.spacedBy(Spacings.Medium),
                        maxItemsInEachRow = 3
                    ) {
                        section.cards.map {
                            CategoryCard(
                                modifier = Modifier.weight(1f),
                                title = it.title,
                                color = it.color,
                                libraryItem = it.libraryItem,
                                onClick = onClickGenre
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    modifier: Modifier,
    title: String,
    color: Color,
    libraryItem: LibraryItem,
    onClick: (String) -> Unit
) {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Card(
        modifier = modifier.fillMaxWidth()
            .aspectRatio(2f)
            .clickable { onClick(title) }
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .onGloballyPositioned { boxSize = it.size }
        ) {
            if (boxSize != IntSize.Zero) {
                val painter =
                    if (LocalInspectionMode.current) painterResource(R.drawable.preview_background)
                    else rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(libraryItem.getBackDropUrl(
                                height = boxSize.height,
                                width = boxSize.width
                            )).size(Size.ORIGINAL).build()
                    )

                Image(
                    painter = painter,
                    contentDescription = "${title} category decoration image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(
                        colors = listOf(Color.Transparent, color.copy(alpha = .6f))
                    )),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center
            ) { Text(color = Color.White, text = title) }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewWithData() {
    val previewLibraryItem = LibraryItem.Movie(
        id = UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )

    val previewList = listOf(previewLibraryItem, previewLibraryItem, previewLibraryItem, previewLibraryItem)

    DashboardScreenContent(
        state = UiState(Step.Success(sections = listOf(
            UiSection.Carousel(
                title = "Continue watching",
                items = previewList
            ),
            UiSection.Carousel(
                title = "Newly added",
                items = previewList
            ),
            UiSection.ButtonCards(cards = listOf(
                ButtonCard("Action", Color.Red, previewLibraryItem),
                ButtonCard("Adventure", Color.Cyan, previewLibraryItem),
                ButtonCard("Anime", Color.Blue, previewLibraryItem),
                ButtonCard("Romance", Color.Magenta, previewLibraryItem),
                ButtonCard("Mystery", Color.White, previewLibraryItem),
                ButtonCard("Horror", Color.Black, previewLibraryItem)
            )),
            UiSection.Carousel(
                title = "My favorites",
                items = previewList
            )
        ))),
        onClickDetailView = {},
        onClickGenre = {}
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewWithLoading() {
    DashboardScreenContent(
        state = UiState(Step.Loading),
        onClickDetailView = {},
        onClickGenre = {}
    )
}
