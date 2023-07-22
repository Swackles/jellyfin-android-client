package com.swackles.jellyfin.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.domain.models.Media
import com.swackles.jellyfin.domain.models.MediaSection
import com.swackles.jellyfin.presentation.common.StateHolder
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.mediaSection.MediaSection
import com.swackles.jellyfin.presentation.destinations.DetailScreenDestination
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Destination
@Composable
fun DashboardScreen(
    viewModal: DashboardViewModal = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state = viewModal.state.value
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.error.isNotBlank()) {
            P(text = state.error, isError = true)
        }
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        LazyColumn {
            itemsIndexed(state.data ?: emptyList()) { _, section ->
                MediaSection(
                    section = section,
                    onClick = {
                        navigator.navigate(DetailScreenDestination(id = it))
                    }
                )
            }
        }
    }
}

@Composable
private fun PreviewWithLoading(isDarkTheme: Boolean) {
    val viewModal = PreviewDashboardViewModal(
        StateHolder(isLoading = true)
    )

    JellyfinTheme(isDarkTheme) {
        DashboardScreen(viewModal, EmptyDestinationsNavigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithLoading_Dark() {
    PreviewWithLoading(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithLoading_White() {
    PreviewWithLoading(false)
}

@Composable
private fun PreviewWithError(isDarkTheme: Boolean) {
    val viewModal = PreviewDashboardViewModal(
        StateHolder(error = "Errored text")
    )

    JellyfinTheme(isDarkTheme) {
        DashboardScreen(viewModal, EmptyDestinationsNavigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithError_Dark() {
    PreviewWithError(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithError_White() {
    PreviewWithError(false)
}

@Composable
private fun PreviewWithData(isDarkTheme: Boolean) {
    val continueWatchingMediaSection = MediaSection(
        "Continue watching",
        listOf(
            Media.preview(),
            Media.preview(),
            Media.preview(),
            Media.preview()
        )
    )
    val newlyAddedMediaSection = MediaSection(
        "Newly added",
        listOf(
            Media.preview(),
            Media.preview(),
            Media.preview(),
            Media.preview()
        )
    )
    val favoritesMediaSection = MediaSection(
        "My favorites",
        listOf(
            Media.preview(),
            Media.preview(),
            Media.preview(),
            Media.preview()
        )
    )
    val viewModal = PreviewDashboardViewModal(
        StateHolder(data = listOf(
            continueWatchingMediaSection,
            newlyAddedMediaSection,
            favoritesMediaSection
        ))
    )

    JellyfinTheme(isDarkTheme) {
        DashboardScreen(viewModal, EmptyDestinationsNavigator)
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