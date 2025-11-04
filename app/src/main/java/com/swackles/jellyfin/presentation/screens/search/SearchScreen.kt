@file:OptIn(ExperimentalLayoutApi::class)

package com.swackles.jellyfin.presentation.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.presentation.components.MediaCard
import com.swackles.jellyfin.presentation.screens.destinations.DetailScreenDestination
import com.swackles.jellyfin.presentation.screens.search.common.FiltersDialog
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.JellyfinFilters
import com.swackles.libs.jellyfin.LibraryFilters
import com.swackles.libs.jellyfin.LibraryItem
import org.jellyfin.sdk.model.UUID

@RootNavGraph
@Destination
@Composable
fun SearchScreen(
    filters: LibraryFilters,
    navigator: DestinationsNavigator,
    viewModal: SearchViewModal = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModal.search(filters)
    }

    SearchScreenContent(
        state = viewModal.state.value,
        onNavigateDetailScreen = {
            navigator.navigate(DetailScreenDestination(id = it))
        },
        onUpdateQuery = { viewModal.updateQuery(newQuery = it) },
        onSearch = { viewModal.search(filters = it) }
    )
}


@Composable
private fun SearchScreenContent(
    state: UiState,
    onNavigateDetailScreen: (id: UUID) -> Unit,
    onUpdateQuery: (String) -> Unit,
    onSearch: (filters: LibraryFilters) -> Unit
) =
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.step) {
            is Step.Loading -> LoadingContent()
            is Step.ShowContent -> ShowContent(
                step = state.step,
                onSearch = onSearch,
                onUpdateQuery = onUpdateQuery,
                onNavigateDetailScreen = onNavigateDetailScreen
            )
            is Step.LoadContent -> SearchScreenContent(
                activeFilters = state.step.activeFilters,
                loading = true,
                onUpdateQuery = {},
                onSearch = {},
                onShowFilters = {},
            ) { LoadingContent() }
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
private fun SearchScreenContent(
    activeFilters: LibraryFilters,
    loading: Boolean,
    onUpdateQuery: (String) -> Unit,
    onSearch: () -> Unit,
    onShowFilters: () -> Unit,
    content: @Composable () -> Unit
) =
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacings.Large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacings.Small),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                enabled = !loading,
                value = activeFilters.query ?: "",
                onValueChange = onUpdateQuery,
                placeholder = { Text("Search") },
                singleLine = true,
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(enabled = !loading, onClick = onShowFilters) {
                Icon(Icons.Default.FilterAlt, contentDescription = "filter")
            }
        }
        content()
    }

@Composable
private fun ShowContent(
    step: Step.ShowContent,
    onUpdateQuery: (String) -> Unit,
    onSearch: (LibraryFilters) -> Unit,
    onNavigateDetailScreen: (id: UUID) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) FiltersDialog(
        possibleFilters = step.possibleFilters,
        defaultFilters = step.activeFilters,
        onCancel = { showDialog = false },
        onSearch = onSearch,
    )

    SearchScreenContent(
        activeFilters = step.activeFilters,
        loading = false,
        onUpdateQuery = onUpdateQuery,
        onSearch = { onSearch(step.activeFilters) },
        onShowFilters = { showDialog = true },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacings.Medium)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(Spacings.Small),
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                step.items.map { item ->
                    MediaCard(media = item, onClick = onNavigateDetailScreen)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoading() {
    val state = UiState(
        step = Step.Loading
    )

    JellyfinTheme {
        SearchScreenContent(
            state = state,
            onNavigateDetailScreen = {},
            onUpdateQuery = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewContentLoading() {
    val state = UiState(
        step = Step.LoadContent(
            possibleFilters = JellyfinFilters(
                genres = emptyList(),
                tags = emptyList(),
                ratings = emptyList(),
                years = emptyList()
            ),
            activeFilters = LibraryFilters(
                genres = emptyList(),
                officialRatings = emptyList(),
                years = emptyList(),
                mediaTypes = emptyList(),
                query = null
            )
        )
    )

    JellyfinTheme {
        SearchScreenContent(
            state = state,
            onNavigateDetailScreen = {},
            onUpdateQuery = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewData() {
    val previewLibraryItem = LibraryItem.Movie(
        id = java.util.UUID.randomUUID(),
        baseUrl = "",
        playedPercentage = 0f,
        playbackPositionTicks = 0L,
        genres = emptyList()
    )

    val state = UiState(
        step = Step.ShowContent(
            possibleFilters = JellyfinFilters(
                genres = emptyList(),
                tags = emptyList(),
                ratings = emptyList(),
                years = emptyList()
            ),
            activeFilters = LibraryFilters(
                genres = emptyList(),
                officialRatings = emptyList(),
                years = emptyList(),
                mediaTypes = emptyList(),
                query = null
            ),
            items = listOf(
                previewLibraryItem, previewLibraryItem, previewLibraryItem,
                previewLibraryItem, previewLibraryItem, previewLibraryItem,
                previewLibraryItem, previewLibraryItem, previewLibraryItem,
                previewLibraryItem, previewLibraryItem, previewLibraryItem,
                previewLibraryItem, previewLibraryItem, previewLibraryItem,
            )
        )
    )

    JellyfinTheme {
        SearchScreenContent(
            state = state,
            onNavigateDetailScreen = {},
            onUpdateQuery = {},
            onSearch = {}
        )
    }
}
