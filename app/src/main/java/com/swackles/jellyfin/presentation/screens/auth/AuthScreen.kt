package com.swackles.jellyfin.presentation.screens.auth

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.screens.destinations.AuthScreenDestination
import com.swackles.jellyfin.presentation.screens.destinations.DashboardScreenDestination

@Destination(start = true)
@Composable
fun AuthScreen(
    navigator: DestinationsNavigator,
    viewModal: AuthViewModal = hiltViewModel()
) {
    AuthScreenContent(
        state = viewModal.state.value,
        onChangeCredentials = viewModal::updateCredentials,
        onLogin = viewModal::login,
        onSuccess = { navigator.navigate(DashboardScreenDestination) {
            popUpTo(AuthScreenDestination) { inclusive = true }
        } }
    )
}

@Composable
private fun AuthScreenContent(
    state: UiState,
    onChangeCredentials: (AuthCredentials) -> Unit,
    onLogin: () -> Unit,
    onSuccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when(state.step) {
            is Step.Loading -> LoadingContent()
            is Step.EnterCredentials ->
                EnterCredentialsContent(
                    state = state.step,
                    onChangeCredentials = onChangeCredentials,
                    onLogin = onLogin
                )
            is Step.Success -> onSuccess()
        }
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
private fun EnterCredentialsContent(
    state: Step.EnterCredentials,
    onChangeCredentials: (newCredentials: AuthCredentials) -> Unit,
    onLogin: () -> Unit
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = state.credentials.hostname,
                label = { P(text = "Host") },
                placeholder = { P(text = "https://localhost:8096") },
                onValueChange = { onChangeCredentials(state.credentials.copy(hostname = it)) },
                isError = state.errors.contains(ErrorKey.HOST),
                supportingText = { P(text = state.errors[ErrorKey.HOST] ?: "") }
            )
            OutlinedTextField(
                value = state.credentials.username,
                label = { P(text = "Username") },
                onValueChange = { onChangeCredentials(state.credentials.copy(username = it)) },
                isError = state.errors.containsKey(ErrorKey.USERNAME),
                supportingText = { P(text = state.errors[ErrorKey.USERNAME] ?: "") }
            )
            PasswordOutlinedTextField(
                value = state.credentials.password,
                label = { P(text = "Password") },
                onValueChange = { onChangeCredentials(state.credentials.copy(password = it)) },
                isError = state.errors.containsKey(ErrorKey.PASSWORD),
                supportingText = { P(text = state.errors[ErrorKey.PASSWORD] ?: "") }
            )
        }
        Button(
            modifier = Modifier.constrainAs(submitButton) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent)
                top.linkTo(inputs.bottom)
            },
            enabled = true,
            onClick = onLogin
        ) {
            P(text = "connect")
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewLoading() {
    AuthScreenContent(state = UiState(Step.Loading), onChangeCredentials = {}, onLogin = {}, onSuccess = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewEnterCredentials() {
    AuthScreenContent(state = UiState(Step.EnterCredentials()), onChangeCredentials = {}, onLogin = {}, onSuccess = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewEnterCredentialsError() {
    AuthScreenContent(state = UiState(Step.EnterCredentials(
        errors = mapOf(
            ErrorKey.HOST to "Incorrect host",
            ErrorKey.USERNAME to "Incorrect username",
            ErrorKey.PASSWORD to "Incorrect password"
        )
    )), onChangeCredentials = {}, onLogin = {}, onSuccess = {})
}

