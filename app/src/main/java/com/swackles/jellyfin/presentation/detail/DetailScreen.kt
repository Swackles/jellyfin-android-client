package com.swackles.jellyfin.presentation.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.jellyfin.sdk.model.UUID

@Destination
@Composable
fun DetailScreen(
    id: UUID,
    navigator: DestinationsNavigator,
    viewModal: DetailScreenViewModal = hiltViewModel()
) {
    val state = viewModal.state.value

    LaunchedEffect(Unit){
        viewModal.loadData(id)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.hasData) DetailScreenLoaded(media = state.data!!, navigator)
    }
}
