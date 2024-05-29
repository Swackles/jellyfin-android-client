package com.swackles.jellyfin.presentation.settings.serversListScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.presentation.common.components.ListItem
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme


@Destination
@Composable
fun ServersListScreen(
    navigator: DestinationsNavigator,
    viewModal: ServersListScreenViewModal = hiltViewModel()
) {
    val servers by viewModal.servers.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Add, "Localized description")
            }
        },
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn {
                items(servers) {
                    ListItem(
                        onClick = { viewModal.setActiveServer(it.id, navigator) },
                        heading = it.name ?: it.host,
                        subHeading = it.host
                    )
                    HorizontalDivider()
                }
            }
        }
    }

}


@Composable
private fun Preview(isDarkTheme: Boolean) {
    val servers = listOf(
        Server(1L, "example.com", "example jellyfin"),
        Server(2L, "example.com", "example jellyfin"),
        Server(3L, "example.com", "example jellyfin")
    )
    val activeServer = 1L

    JellyfinTheme(isDarkTheme) {
        ServersListScreen(
            EmptyDestinationsNavigator, SettingsViewModalPreview(
                activeServer, servers
            )
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