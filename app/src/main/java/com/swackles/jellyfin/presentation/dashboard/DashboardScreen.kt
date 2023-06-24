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
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.mediaSection.MediaSection

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreen(
    viewModal: DashboardViewModal = hiltViewModel()
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

                    }
                )
            }
        }
    }
}
