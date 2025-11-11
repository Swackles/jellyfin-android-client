package com.swackles.jellyfin.presentation.screens.auth.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.swackles.jellyfin.presentation.components.ProfileCard
import com.swackles.jellyfin.presentation.screens.auth.AuthGraph
import com.swackles.jellyfin.presentation.styles.JellyfinTheme
import com.swackles.jellyfin.presentation.styles.Spacings
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
import java.util.UUID

@Destination<AuthGraph>
@Composable
fun UserSelectScreen(
    serverId: UUID,
    navigator: DestinationsNavigator,
    viewModal: UserSelectViewModal = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModal.initialize(serverId = serverId)
    }

    UserSelectScreenContent(
        state = viewModal.state.value,
        onSelectUser = viewModal::selectUser,
        onAddUser = viewModal::addUser,
        onNavigate = { navigator.navigate(it) }
    )
}

@Composable
private fun UserSelectScreenContent(
    state: UiState,
    onSelectUser: (UUID) -> Unit,
    onAddUser: () -> Unit,
    onNavigate: (Direction) -> Unit
) =
    when(state.step) {
        is Step.Loading -> LoadingContent()
        is Step.Select -> SelectUserContent(
            state = state.step,
            onSelectUser = onSelectUser,
            onAddUser = onAddUser
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
private fun SelectUserContent(
    state: Step.Select,
    onSelectUser: (UUID) -> Unit,
    onAddUser: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Spacings.Small, vertical = Spacings.Large),
        horizontalArrangement = Arrangement.spacedBy(Spacings.Medium, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(Spacings.Medium, Alignment.CenterVertically)
    ) {
        state.sessions.map { ProfileCard(session = it, active = false, onClick = onSelectUser) }
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
    val sessionThree = Session(
        id = UUID.randomUUID(),
        server = Server(
            id = UUID.randomUUID(),
            hostname = "localhost:80",
            name = "Server 3"
        ),
        profileImageUrl = "",
        username = "Session 3",
        token = ""
    )
    val sessionFour = Session(
        id = UUID.randomUUID(),
        server = Server(
            id = UUID.randomUUID(),
            hostname = "localhost:80",
            name = "Server 4"
        ),
        profileImageUrl = "",
        username = "Session 4",
        token = ""
    )

    JellyfinTheme {
        UserSelectScreenContent(
            state = UiState(
                step = Step.Select(
                    sessions = listOf(sessionOne, sessionTwo, sessionThree, sessionFour)
                )
            ),
            onSelectUser = {},
            onNavigate = {},
            onAddUser = {}
        )
    }
}