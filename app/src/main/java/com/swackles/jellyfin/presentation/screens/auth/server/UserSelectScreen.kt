package com.swackles.jellyfin.presentation.screens.auth.server

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.swackles.jellyfin.presentation.components.ListItem
import com.swackles.jellyfin.presentation.screens.auth.AuthGraph
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.jellyfin.session.Server
import java.util.UUID

@Destination<AuthGraph>(start = true)
@Composable
fun ServerSelectScreen(
    navigator: DestinationsNavigator,
    viewModal: ServerSelectViewModal = hiltViewModel()
) {
    LaunchedEffect(viewModal) {
        viewModal.initialize()
    }

    Log.d("ServerSelectScreen", viewModal.state.toString())
    ServerSelectScreenContent(
        state = viewModal.state.value,
        onSelectServer = viewModal::selectServer,
        onNewServer = viewModal::newServer,
        onNavigate = {
            navigator.navigate(it)
            viewModal.onNavigationHandled()
        }
    )
}

@Composable
private fun ServerSelectScreenContent(
    state: UiState,
    onSelectServer: (UUID) -> Unit,
    onNewServer: () -> Unit,
    onNavigate: (Direction) -> Unit
) =
    when(state.step) {
        is Step.Loading -> LoadingContent()
        is Step.Select -> SelectContent(
            state = state.step,
            onServerSelect = onSelectServer,
            onNewServer = onNewServer
        )
        is Step.NavigateTo -> onNavigate(state.step.route)
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
private fun SelectContent(
    state: Step.Select,
    onServerSelect: (UUID) -> Unit,
    onNewServer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Spacings.Small, vertical = Spacings.Large),
        verticalArrangement = Arrangement.Center
    ) {
        state.servers.map {
            ListItem(
                onClick = { onServerSelect(it.id) },
                heading = it.name,
                subHeading = it.hostname,
                leadingIcon = Icons.Filled.Dns
            )
            HorizontalDivider()
        }
        ListItem(
            onClick = onNewServer,
            heading = "New Server",
            leadingIcon = Icons.Filled.Add
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    JellyfinTheme {
        ServerSelectScreenContent(
            state = UiState(
                step = Step.Select(
                    servers = listOf(
                        Server(id = UUID.randomUUID(), hostname = "localhost:80", name = "Server 1"),
                        Server(id = UUID.randomUUID(), hostname = "localhost:80", name = "Server 2"),
                        Server(id = UUID.randomUUID(), hostname = "localhost:80", name = "Server 3"),
                        Server(id = UUID.randomUUID(), hostname = "localhost:80", name = "Server 4")
                    )
                )
            ),
            onSelectServer = {},
            onNavigate = {},
            onNewServer = {}
        )
    }
}