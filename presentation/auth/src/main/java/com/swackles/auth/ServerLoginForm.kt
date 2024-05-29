package com.swackles.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.PasswordOutlinedTextField
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme


@Composable
fun ServerLoginForm(
    responseState: ServerLoginFormResponseState,
    onSave: (inputs: AuthCredentials) -> Unit
) {
    var host by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isValidInput = host.isNotBlank() && username.isNotBlank() && password.isNotBlank()

    Surface {
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
                    value = host,
                    label = { P(text = "Host") },
                    readOnly = responseState.isLoading,
                    placeholder = { P(text = "https://localhost:8096") },
                    onValueChange = { host = it },
                    isError = responseState.errors.containsKey(ErrorKey.HOST),
                    supportingText = { P(text = responseState.errors[ErrorKey.HOST] ?: "") })
                OutlinedTextField(
                    value = username,
                    label = { P(text = "Username") },
                    readOnly = responseState.isLoading,
                    onValueChange = { username = it },
                    isError = responseState.errors.containsKey(ErrorKey.USERNAME),
                    supportingText = { P(text = responseState.errors[ErrorKey.USERNAME] ?: "") })
                PasswordOutlinedTextField(
                    value = password,
                    label = { P(text = "Password") },
                    readOnly = responseState.isLoading,
                    onValueChange = { password = it },
                    isError = responseState.errors.containsKey(ErrorKey.PASSWORD),
                    supportingText = { P(text = responseState.errors[ErrorKey.PASSWORD] ?: "") })
            }
            Button(
                modifier = Modifier.constrainAs(submitButton) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                    top.linkTo(inputs.bottom)
                },
                enabled = isValidInput && !responseState.isLoading,
                onClick = {
                    onSave(AuthCredentials(host.trim(), username.trim(), password.trim()))
                }
            ) {
                P(text = "connect")
            }
        }
    }
}


@Composable
private fun Preview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        ServerLoginForm(
            responseState = ServerLoginFormResponseState(
                errors = mapOf(
                    ErrorKey.HOST to "Invalid host",
                    ErrorKey.USERNAME to "Invalid username",
                    ErrorKey.PASSWORD to "Invalid password"
                ),
                isLoading = false
            ),
            onSave = { }
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