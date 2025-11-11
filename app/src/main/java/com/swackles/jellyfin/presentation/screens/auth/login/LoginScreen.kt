package com.swackles.jellyfin.presentation.screens.auth.login

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.swackles.jellyfin.presentation.components.MediumText
import com.swackles.jellyfin.session.Server
import java.util.UUID

@Destination<RootGraph>
@Composable
fun LoginScreen(
    server: Server? = null,
    viewModal: LoginViewModal = hiltViewModel()
) {
    LaunchedEffect(Unit){
        viewModal.initialize(server)
    }

    LoginScreenContent(
        state = viewModal.state.value,
        onChangeCredentials = viewModal::updateCredentials,
        onLogin = viewModal::login
    )
}

@Composable
private fun LoginScreenContent(
    state: UiState,
    onChangeCredentials: (AuthCredentials) -> Unit,
    onLogin: () -> Unit
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
            if (state.credentials is AuthCredentials.WithHostname) {
                OutlinedTextField(
                    value = state.credentials.hostname,
                    label = { MediumText(text = "Host") },
                    placeholder = { MediumText(text = "https://localhost:8096") },
                    onValueChange = { onChangeCredentials(state.credentials.copy(hostname = it)) },
                    isError = state.errors.contains(ErrorKey.HOST),
                    supportingText = { MediumText(text = state.errors[ErrorKey.HOST] ?: "") }
                )
            }
            OutlinedTextField(
                value = state.credentials.username,
                label = { MediumText(text = "Username") },
                onValueChange = { onChangeCredentials(when(state.credentials) {
                    is AuthCredentials.WithHostname -> state.credentials.copy(username = it)
                    is AuthCredentials.WithServer -> state.credentials.copy(username = it)
                }) },
                isError = state.errors.containsKey(ErrorKey.USERNAME),
                supportingText = { MediumText(text = state.errors[ErrorKey.USERNAME] ?: "") }
            )
            PasswordOutlinedTextField(
                value = state.credentials.password,
                label = { MediumText(text = "Password") },
                onValueChange = { onChangeCredentials(when(state.credentials) {
                    is AuthCredentials.WithHostname -> state.credentials.copy(password = it)
                    is AuthCredentials.WithServer -> state.credentials.copy(password = it)
                }) },
                isError = state.errors.containsKey(ErrorKey.PASSWORD),
                supportingText = { MediumText(text = state.errors[ErrorKey.PASSWORD] ?: "") }
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
            MediumText(text = "connect")
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewLoading() {
    LoginScreenContent(state = UiState(Step.Loading), onChangeCredentials = {}, onLogin = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewEnterCredentials() {
    LoginScreenContent(state = UiState(Step.EnterCredentials(
        credentials = AuthCredentials.WithHostname()
    )), onChangeCredentials = {}, onLogin = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewEnterCredentialsWithoutHostname() {
    LoginScreenContent(state = UiState(Step.EnterCredentials(
        credentials = AuthCredentials.WithServer(
            server = Server(id = UUID.randomUUID(), hostname = "", name = "string")
        )
    )), onChangeCredentials = {}, onLogin = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewEnterCredentialsError() {
    LoginScreenContent(state = UiState(Step.EnterCredentials(
        credentials = AuthCredentials.WithHostname(),
        errors = mapOf(
            ErrorKey.HOST to "Incorrect host",
            ErrorKey.USERNAME to "Incorrect username",
            ErrorKey.PASSWORD to "Incorrect password"
        )
    )), onChangeCredentials = {}, onLogin = {})
}

