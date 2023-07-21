package com.swackles.jellyfin.presentation.server

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.domain.models.Server
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.PasswordOutlinedTextField
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme
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
            ServerScreenInput(
                state = viewModal.getState(),
                onChange = viewModal::updateState,
                onSave = { coroutineScope.launch { viewModal.saveServer(navigator) } }
            )
        }
    }
}

@Composable
private fun ServerScreenInput(
    state: ServerUiState,
    onChange: (server: Server) -> Unit,
    onSave: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (submitButton, inputs) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(inputs) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                },
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = state.server.host,
                label = { P("Host") },
                readOnly = state.isLoading,
                placeholder = { P("https://localhost:8096") },
                onValueChange = { onChange(state.server.copy(host = it)) },
                isError = state.errors.containsKey(ErrorKey.HOST),
                supportingText = { P(state.errors[ErrorKey.HOST] ?: "") })
            OutlinedTextField(
                value = state.server.username,
                label = { P("Username") },
                readOnly = state.isLoading,
                onValueChange = { onChange(state.server.copy(username = it)) },
                isError = state.errors.containsKey(ErrorKey.USERNAME),
                supportingText = { P(state.errors[ErrorKey.USERNAME] ?: "") })
            PasswordOutlinedTextField(
                value = state.server.password,
                label = { P("Password") },
                readOnly = state.isLoading,
                onValueChange = { onChange(state.server.copy(password = it)) },
                isError = state.errors.containsKey(ErrorKey.PASSWORD),
                supportingText = { P(state.errors[ErrorKey.PASSWORD] ?: "") })
        }
        Button(
            modifier = Modifier.constrainAs(submitButton) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent)
                top.linkTo(inputs.bottom)
            },
            enabled = state.isValidInput && !state.isLoading,
            onClick = onSave
        ) {
            P("connect")
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
            server = Server(),
            isValidInput = false,
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
private fun PreviewServerScreenWithValidInput(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(
            server = Server(
                host = "http://localhost:8096",
                username = "Swackles"
            ),
            isValidInput = true,
            isInitializing = false
        )
    )
    JellyfinTheme(isDarkTheme) {
        ServerScreen(EmptyDestinationsNavigator, viewModal)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenWithValidInput_Dark() {
    PreviewServerScreenWithValidInput(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewServerScreenWithValidInput_White() {
    PreviewServerScreenWithValidInput(false)
}

@Composable
private fun PreviewServerScreenLoading(isDarkTheme: Boolean) {
    val viewModal = PreviewServerViewModal(
        ServerUiState(
            server = Server(
                host = "http://localhost:8096",
                username = "Swackles",
                password = "Password"
            ),
            isValidInput = true,
            isLoading = true,
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
            server = Server(
                host = "http://localhost:8096",
                username = "Swackles",
                password = "Password"
            ),
            isValidInput = true,
            isLoading = false,
            isInitializing = false,
            errors = emptyMap<ErrorKey, String>()
                .plus(Pair(ErrorKey.HOST, "Cannot connect"))
                .plus(Pair(ErrorKey.USERNAME, "Invalid username or password"))
                .plus(Pair(ErrorKey.PASSWORD, "Invalid username or password"))
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
