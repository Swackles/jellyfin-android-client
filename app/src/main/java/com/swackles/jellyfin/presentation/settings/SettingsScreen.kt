package com.swackles.jellyfin.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.auth.ServerLoginFormDialog
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.presentation.common.components.ListItem
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme
import com.swackles.jellyfin.presentation.destinations.ServersListScreenDestination
import com.swackles.jellyfin.presentation.settings.components.AddUserCard
import com.swackles.jellyfin.presentation.settings.components.ProfileCard
import kotlinx.coroutines.launch

@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModal: SettingsViewModal = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val state = viewModal.state.value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp),
                state = rememberLazyListState()
            ) {
                item { Spacer(modifier = Modifier.width((screenWidth / 2) - 60.dp)) }
                itemsIndexed(state.users) { _, user ->
                    ProfileCard(user, user.id == state.activeUser.id, onClick = viewModal::login)
                }
                item { AddUserCard(onClick = viewModal::toggleModalVisibility) }
                item { Spacer(modifier = Modifier.width((screenWidth / 2) - 60.dp)) }
            }
            ListItem(
                onClick = { navigator.navigate(ServersListScreenDestination) },
                heading = "Servers",
                leadingIcon = Icons.Filled.Dns
            )
            HorizontalDivider()
            ListItem(
                onClick = { /*TODO*/ },
                heading = "Quick Connect",
                leadingIcon = Icons.Filled.LockOpen
            )
            HorizontalDivider()
            ListItem(
                onClick = { /*TODO*/ },
                heading = "Account",
                leadingIcon = Icons.Filled.AccountCircle
            )
            HorizontalDivider()
            ListItem(
                onClick = { /*TODO*/ },
                heading = "Jellyfin Settings",
                leadingIcon = Icons.Filled.Settings
            )
            HorizontalDivider()
            ListItem(
                onClick = { /*TODO*/ },
                heading = "App Settings",
                leadingIcon = Icons.Filled.Settings
            )
            HorizontalDivider()
            ListItem(
                onClick = { coroutineScope.launch { viewModal.logoutServer(navigator) } },
                heading = "Logout from server",
                leadingIcon = Icons.AutoMirrored.Filled.Logout
            )
            HorizontalDivider()
            ListItem(
                onClick = { coroutineScope.launch { viewModal.logoutUser(navigator) } },
                heading = "Logout",
                leadingIcon = Icons.AutoMirrored.Filled.Logout
            )
        }

        if (state.isAddUserModalVisible) {
            ServerLoginFormDialog(
                responseState = ServerLoginFormResponseState(isLoading = state.isLoading),
                showHost = false,
                onDismiss = viewModal::toggleModalVisibility,
                onLogin = { viewModal.login(it) }
            )
        }
    }
}


@Composable
private fun Preview(isDarkTheme: Boolean) {
    val userOne = User.preview().copy(id = 0)
    val userTwo = User.preview().copy(id = 1)

    val state = SettingsViewModalState(
        users = listOf(userOne, userTwo),
        activeUser = userOne
    )
    JellyfinTheme(isDarkTheme) {
        SettingsScreen(EmptyDestinationsNavigator, SettingsViewModalPreview(state))
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

@Composable
private fun Preview_addUserModal(isDarkTheme: Boolean) {
    val userOne = User.preview().copy(id = 0)
    val userTwo = User.preview().copy(id = 1)

    val state = SettingsViewModalState(
        users = listOf(userOne, userTwo),
        activeUser = userOne,
        isAddUserModalVisible = true
    )
    JellyfinTheme(isDarkTheme) {
        SettingsScreen(EmptyDestinationsNavigator, SettingsViewModalPreview(state))
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_addUserModal_Dark() {
    Preview_addUserModal(true)
}

@Preview(showBackground = true)
@Composable
private fun Preview_addUserModal_White() {
    Preview_addUserModal(false)
}
