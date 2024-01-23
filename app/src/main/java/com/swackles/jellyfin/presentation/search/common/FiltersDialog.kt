package com.swackles.jellyfin.presentation.search.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swackles.jellyfin.data.models.MediaFilters
import com.swackles.jellyfin.presentation.common.FilterChip
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Composable
fun FiltersDialog(
    items: MediaFilters,
    selectedItems: MediaFilters,
    isFiltersDialogVisible: Boolean,

    toggleVisibility: () -> Unit,
    updateSelectedItems: (MediaFilters) -> Unit = { }
) {
    if (!isFiltersDialogVisible) return

    Dialog(onDismissRequest = toggleVisibility) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 600.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(8.dp)) {
                FiltersGroup(title = "Filters", items = items.genres, selectedItems = selectedItems.genres, onClick = {
                    updateSelectedItems(selectedItems.copy(genres = it))
                })
                FiltersGroup(title = "Official Ratings", items = items.officialRatings, selectedItems = selectedItems.officialRatings, onClick = {
                    updateSelectedItems(selectedItems.copy(officialRatings = it))
                })
                FiltersGroup(title = "Years", items = items.years, selectedItems = selectedItems.years, onClick = {
                    updateSelectedItems(selectedItems.copy(years = it))
                })
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
) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
    Divider()
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        items.map { item ->
            val selected = selectedItems.contains(item)
            FilterChip(value = item.toString(),selected = selected, onClick = {
                if (selected) {
                    onClick(selectedItems.filter { item != it })
                } else {
                    onClick(selectedItems + item)
                }
            })
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun PreviewWithData(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        FiltersDialog(
            items = MediaFilters(
                genres = listOf("Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller"),
                officialRatings = listOf("G", "PG", "PG-13", "R", "NC-17"),
                years = listOf(2021, 2020, 2019, 2018, 2017, 2016, 2015)
            ),
            selectedItems = MediaFilters(
                genres = listOf("Action", "Horror", "Romance"),
                officialRatings = listOf("PG-13", "NC-17"),
                years = listOf(2018)
            ),
            isFiltersDialogVisible = true,
            toggleVisibility = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithData_Dark() {
    PreviewWithData(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithData_White() {
    PreviewWithData(false)
}

@Composable
private fun PreviewWithoutData(isDarkTheme: Boolean) {

    JellyfinTheme(isDarkTheme) {
        FiltersDialog(
            items = MediaFilters(emptyList(), emptyList(), emptyList()),
            selectedItems = MediaFilters(emptyList(), emptyList(), emptyList()),
            isFiltersDialogVisible = true,
            toggleVisibility = { },
            updateSelectedItems = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithoutData_Dark() {
    PreviewWithoutData(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithoutData_White() {
    PreviewWithoutData(false)
}

@Composable
private fun Preview(isDarkTheme: Boolean) {

    JellyfinTheme(isDarkTheme) {
        FiltersDialog(
            items = MediaFilters(emptyList(), emptyList(), emptyList()),
            selectedItems = MediaFilters(emptyList(), emptyList(), emptyList()),
            isFiltersDialogVisible = true,
            toggleVisibility = { },
            updateSelectedItems = { }
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
