package com.swackles.jellyfin.presentation.screens.search.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.components.LargeTitle
import com.swackles.jellyfin.presentation.components.MediumText
import com.swackles.jellyfin.presentation.components.MediumTitle
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.libs.jellyfin.JellyfinFilters
import com.swackles.libs.jellyfin.LibraryFilters

@Composable
fun FiltersDialog(
    possibleFilters: JellyfinFilters,
    defaultFilters: LibraryFilters,
    onCancel: () -> Unit,
    onSearch: (LibraryFilters) -> Unit
) {
    var filters by remember { mutableStateOf(defaultFilters) }

    Dialog(onDismissRequest = onCancel) {
        Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(Spacings.Medium),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacings.Medium)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacings.Large),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCancel) {
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back")
                        }
                        LargeTitle(text = "Filters")
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        FiltersGroup(
                            title = "Filters",
                            items = possibleFilters.genres,
                            selectedItems = filters.genres,
                            onClick = { filters = filters.copy(genres = it) }
                        )
                        FiltersGroup(
                            title = "Official Ratings",
                            items = possibleFilters.ratings,
                            selectedItems = filters.officialRatings,
                            onClick = { filters = filters.copy(officialRatings = it) }
                        )
                        FiltersGroup(
                            title = "Years",
                            items = possibleFilters.years,
                            selectedItems = filters.years,
                            onClick = { filters = filters.copy(years = it) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { onSearch(filters) }) {
                        MediumText(text = "Search")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T>FiltersGroup(
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    onClick: (List<T>) -> Unit
) =
    Column {
        MediumTitle(text = title)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Small),
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
                .padding(Spacings.Small)
        ) {
            items.map { item ->
                val selected = selectedItems.contains(item)

                FilterChip(
                    label = { Text(item.toString()) },
                    selected = selected,
                    onClick = {
                        if (selected) {
                            onClick(selectedItems.filter { item != it })
                        } else {
                            onClick(selectedItems + item)
                        }
                    }
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
private fun PreviewWithData() {
    JellyfinTheme {
        FiltersDialog(
            possibleFilters = JellyfinFilters(
                genres = listOf("Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller"),
                tags = listOf(""),
                ratings = listOf("G", "PG", "PG-13", "R", "NC-17"),
                years = listOf(2021, 2020, 2019, 2018, 2017, 2016, 2015)
            ),
            defaultFilters = LibraryFilters(
                genres = listOf("Action", "Horror", "Romance"),
                officialRatings = listOf("PG-13", "NC-17"),
                years = listOf(2018),
                mediaTypes = emptyList(),
                query = null
            ),
            onCancel = { },
            onSearch = { }
        )
    }
}
