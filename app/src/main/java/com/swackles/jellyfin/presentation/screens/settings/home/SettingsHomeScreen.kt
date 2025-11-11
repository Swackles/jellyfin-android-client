package com.swackles.jellyfin.presentation.screens.settings.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.swackles.jellyfin.presentation.components.ListItem
import com.swackles.jellyfin.presentation.components.ProfileCard
import com.swackles.jellyfin.presentation.components.ProfileCardProps
import com.swackles.jellyfin.presentation.screens.settings.SettingsGraph
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import java.util.UUID

@Destination<SettingsGraph>(start = true)
@Composable
fun SettingsHomeScreen(
    navigator: DestinationsNavigator,
    viewModal: SettingsHomeViewModal = hiltViewModel()
) {
    SettingsHomeScreenContent(
        state = viewModal.state.value,
        onAddUser = viewModal::addUser,
        onLogoutSession = viewModal::logoutSession,
        onLogoutServer = viewModal::logoutServer,
        onSwitchSession = viewModal::switchSession,
        onNavigate = { navigator.navigate(it) }
    )
}

@Composable
private fun SettingsHomeScreenContent(
    state: UiState,
    onAddUser: () -> Unit,
    onLogoutSession: () -> Unit,
    onLogoutServer: () -> Unit,
    onSwitchSession: (UUID) -> Unit,
    onNavigate: (Direction) -> Unit
) =
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when(state.step) {
            is Step.Loading -> LoadingContent()
            is Step.NavigateTo -> onNavigate(state.step.route)
            is Step.Success -> Content(
                step = state.step,
                onAddUser = onAddUser,
                onLogoutSession = onLogoutSession,
                onLogoutServer = onLogoutServer,
                onSwitchSession = onSwitchSession,
                onNavigate = onNavigate
            )
        }
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
private fun Content(
    step: Step.Success,
    onAddUser: () -> Unit,
    onLogoutSession: () -> Unit,
    onLogoutServer: () -> Unit,
    onSwitchSession: (UUID) -> Unit,
    onNavigate: (Direction) -> Unit,
) {
    val rowState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        val index = step.sessions.indexOfFirst { it.id == step.activeSession.id } + 1

        val halfScreen = screenWidth / 2
        val halfItem = ProfileCardProps.SIZE / 2

        val offsetPx = with(density) {
            (halfScreen - halfItem).toPx()
        }

        rowState.scrollToItem(index, -offsetPx.toInt())
    }

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacings.xxl),
            horizontalArrangement = Arrangement.spacedBy(Spacings.xxl),
            state = rowState
        ) {
            item { Spacer(modifier = Modifier.width((screenWidth / 2) - 60.dp)) }
            itemsIndexed(step.sessions) { _, session ->
                ProfileCard(
                    session = session,
                    active = session.id == step.activeSession.id,
                    onClick = onSwitchSession
                )
            }
            item {
                OutlinedCard(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable(onClick = onAddUser)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add user",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.width((screenWidth / 2) - 60.dp)) }
        }
        ListItem(
            onClick = { { TODO("Implement switch server") } },
            heading = "Servers",
            leadingIcon = Icons.Filled.Dns
        )
        HorizontalDivider()
        ListItem(
            onClick = { TODO("Implement quick connect") },
            heading = "Quick Connect",
            leadingIcon = Icons.Filled.LockOpen
        )
        HorizontalDivider()
        ListItem(
            onClick = { TODO("Implement account settings") },
            heading = "Account",
            leadingIcon = Icons.Filled.AccountCircle
        )
        HorizontalDivider()
        ListItem(
            onClick = { TODO("Implement jellyfin settings") },
            heading = "Jellyfin Settings",
            leadingIcon = Icons.Filled.Settings
        )
        HorizontalDivider()
        ListItem(
            onClick = { TODO("Implement app settings") },
            heading = "App Settings",
            leadingIcon = Icons.Filled.Settings
        )
        HorizontalDivider()
        ListItem(
            onClick = onLogoutServer,
            heading = "Logout from server",
            leadingIcon = Icons.AutoMirrored.Filled.Logout
        )
        HorizontalDivider()
        ListItem(
            onClick = onLogoutSession,
            heading = "Logout",
            leadingIcon = Icons.AutoMirrored.Filled.Logout
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    val sessionOne = Session(
        id = UUID.randomUUID(),
        server = Server(
            id = UUID.randomUUID(),
            hostname = "localhost:80",
            name = "Server 1"
        ),
        profileImageUrl = "",
        username = "Session 1",
        token = ""
    )
    val sessionTwo = Session(
        id = UUID.randomUUID(),
        server = Server(
            id = UUID.randomUUID(),
            hostname = "localhost:80",
            name = "Server 2"
        ),
        profileImageUrl = "",
        username = "Session 2",
        token = ""
    )

    val step = Step.Success(
        sessions = listOf(sessionOne, sessionTwo),
        activeSession = sessionTwo
    )

    JellyfinTheme {
        SettingsHomeScreenContent(
            state = UiState(step = step),
            onAddUser = {  },
            onLogoutSession = {  },
            onLogoutServer = {  },
            onSwitchSession = {  },
            onNavigate = {  },
        )
    }
}
