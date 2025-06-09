package com.swackles.jellyfin.presentation.search.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.jellyfin.enums.MediaItemType
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme

@Composable
fun SearchScreenFilters(
    filterItems: PossibleFilters,
    searchItems: (filters: MediaFilters) -> Unit
) {
    var isFiltersDialogVisible by remember { mutableStateOf(false) }
    var selectedFiltersDialogItems by rememberSaveable { mutableStateOf(MediaFilters()) }
    var selectedMediaItem by rememberSaveable { mutableStateOf<MediaItemType?>(null) }
    var query by rememberSaveable { mutableStateOf("") }

    fun search() {
        searchItems(
            MediaFilters(
                query = query,
                mediaTypes = if (selectedMediaItem == null) listOf(MediaItemType.MOVIE, MediaItemType.SERIES) else listOf(selectedMediaItem!!),
                genres = selectedFiltersDialogItems.genres,
                years = selectedFiltersDialogItems.years,
                officialRatings = selectedFiltersDialogItems.officialRatings
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        FiltersDialog(
            items = filterItems,
            selectedItems = selectedFiltersDialogItems,
            isFiltersDialogVisible = isFiltersDialogVisible,
            toggleVisibility = { isFiltersDialogVisible = !isFiltersDialogVisible },
            updateSelectedItems = { selectedFiltersDialogItems = it }
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = getTabIndex(selectedMediaItem),
                    tabs = {
                        Tab(
                            selected = false,
                            onClick = { selectedMediaItem = null },
                            text = { Text(text = "All", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                        Tab(
                            selected = false,
                            onClick = { selectedMediaItem = MediaItemType.MOVIE },
                            text = { Text(text = "Movies", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                        Tab(
                            selected = false,
                            onClick = { selectedMediaItem = MediaItemType.SERIES },
                            text = { Text(text = "Series", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search") },
                        singleLine = true,
                        keyboardActions = KeyboardActions (
                            onSearch = { search() }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { isFiltersDialogVisible = !isFiltersDialogVisible }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "filter")
                    }
                }

            }
        }
    }
}

private fun getTabIndex(mediaItemType: MediaItemType?): Int {
    return when(mediaItemType) {
        MediaItemType.MOVIE -> 1
        MediaItemType.SERIES -> 2
        else -> 0
    }
}

@Composable
private fun Preview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        SearchScreenFilters(
            filterItems = PossibleFilters(emptyList(), emptyList(), emptyList(), emptyList()),
            searchItems = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_Dark() {
    Preview(true)
}

@Preview(showBackground = true)
@Composable
private fun Preview_White() {
    Preview(false)
}
