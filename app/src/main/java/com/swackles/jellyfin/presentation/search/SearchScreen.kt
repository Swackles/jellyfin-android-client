package com.swackles.jellyfin.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.data.jellyfin.models.Media
import com.swackles.jellyfin.data.jellyfin.models.MediaFilters
import com.swackles.jellyfin.presentation.common.StateHolder
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.mediaSection.MediaCard
import com.swackles.jellyfin.presentation.destinations.DetailScreenDestination
import com.swackles.jellyfin.presentation.search.common.SearchScreenFilters
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@OptIn(ExperimentalLayoutApi::class)
@Destination
@Composable
fun SearchScreen(
    viewModal: SearchViewModal = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val mediaItemError = viewModal.mediaItemsState.value.error
    val mediaFilterError = viewModal.mediaFiltersState.value.error

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (mediaItemError.isNotBlank() || mediaFilterError.isNotBlank()) {
            if (mediaItemError.isNotBlank()) {
                P(text = mediaItemError, isError = true)
            }
            if (mediaFilterError.isNotBlank()) {
                P(text = mediaFilterError, isError = true)
            }
        } else {
            Column {
                SearchScreenFilters(
                    filterItems = viewModal.mediaFiltersState.value.data ?: MediaFilters(),
                    searchItems = viewModal::searchItems
                )
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    (viewModal.mediaItemsState.value.data ?: emptyList()).map { item ->
                        MediaCard(media = item, onClick = { navigator.navigate(DetailScreenDestination(it))})
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewWithData(isDarkTheme: Boolean) {
    val viewModal = PreviewSearchViewModal(
        StateHolder(
            data = MediaFilters(
                genres = emptyList(),
                officialRatings = emptyList(),
                years = emptyList()
            )
        ),
        StateHolder(
            data = listOf(
                Media.preview(),
                Media.preview(),
                Media.preview(),
                Media.preview()
            )
        )
    )


    JellyfinTheme(isDarkTheme) {
        SearchScreen(viewModal, EmptyDestinationsNavigator)
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
private fun PreviewWithFiltersError(isDarkTheme: Boolean) {
    val viewModal = PreviewSearchViewModal(
        StateHolder(
            error = "Error getting filters"
        ),
        StateHolder(
            data = listOf(
                Media.preview(),
                Media.preview(),
                Media.preview(),
                Media.preview()
            )
        )
    )


    JellyfinTheme(isDarkTheme) {
        SearchScreen(viewModal, EmptyDestinationsNavigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithFiltersError_Dark() {
    PreviewWithFiltersError(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithFiltersError_White() {
    PreviewWithFiltersError(false)
}

@Composable
private fun PreviewWithMediaError(isDarkTheme: Boolean) {
    val viewModal = PreviewSearchViewModal(
        StateHolder(
            data = MediaFilters(
                genres = emptyList(),
                officialRatings = emptyList(),
                years = emptyList()
            )
        ),
        StateHolder(
            error = "Error getting media"
        )
    )


    JellyfinTheme(isDarkTheme) {
        SearchScreen(viewModal, EmptyDestinationsNavigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMediaError_Dark() {
    PreviewWithMediaError(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMediaError_White() {
    PreviewWithMediaError(false)
}

@Composable
private fun PreviewWithBothError(isDarkTheme: Boolean) {
    val viewModal = PreviewSearchViewModal(
        StateHolder(
            error = "Error getting filters"
        ),
        StateHolder(
            error = "Error getting media"
        )
    )

    JellyfinTheme(isDarkTheme) {
        SearchScreen(viewModal, EmptyDestinationsNavigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithBothError_Dark() {
    PreviewWithBothError(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithBothError_White() {
    PreviewWithBothError(false)
}
