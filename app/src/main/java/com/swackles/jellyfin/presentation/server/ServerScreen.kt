package com.swackles.jellyfin.presentation.server

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.auth.ServerLoginForm
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun ServerScreen(
    navigator: DestinationsNavigator,
    viewModal: ServerViewModal = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    viewModal.init(navigator)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (viewModal.getState().isInitializing) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            ServerLoginForm(
                responseState = viewModal.authResponseState,
                onSave = { coroutineScope.launch { viewModal.saveServer(it, navigator) } }
            )
        }
    }
}


@Composable
private fun PreviewWithInitialization(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(isInitializing = true)
    )

    JellyfinTheme(isDarkTheme) {
        ServerScreen(EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithInitialization_Dark() {
    PreviewWithInitialization(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithInitialization_White() {
    PreviewWithInitialization(false)
}

@Composable
private fun PreviewServerScreenInput(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(
            isInitializing = false
        )
    )

    JellyfinTheme(isDarkTheme) {
        ServerScreen(EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenInput_Dark() {
    PreviewServerScreenInput(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenInput_White() {
    PreviewServerScreenInput(false)
}

@Composable
private fun PreviewServerScreenLoading(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(
            isInitializing = false
        )
    )
    JellyfinTheme(isDarkTheme) {
        ServerScreen(EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenLoading_Dark() {
    PreviewServerScreenLoading(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenLoading_White() {
    PreviewServerScreenLoading(false)
}

@Composable
private fun PreviewServerScreenErrors(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(
            isInitializing = false
        )
    )
    JellyfinTheme(isDarkTheme) {
        ServerScreen(EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenErrors_Dark() {
    PreviewServerScreenErrors(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenErrors_White() {
    PreviewServerScreenErrors(false)
}
