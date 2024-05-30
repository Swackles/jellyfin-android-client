package com.swackles.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.presentation.common.colors.errorTextButtonColors
import com.swackles.jellyfin.presentation.common.colors.primaryTextButtonColors
import com.swackles.jellyfin.presentation.common.components.H2
import com.swackles.jellyfin.presentation.common.components.Label
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerLoginFormDialog(
    responseState: ServerLoginFormResponseState,
    showHost: Boolean = true,
    onDismiss: () -> Unit,
    onLogin: (credentials: AuthCredentials) -> Unit
) {
    var authCredentials by remember { mutableStateOf(AuthCredentials()) }

    BasicAlertDialog(
        onDismissRequest = { /*TODO*/ }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                H2(text = if (showHost) "Login to Jellyfin" else "Add User")
                Spacer(modifier = Modifier.height(16.dp))
                ServerLoginInputs(
                    showHost = showHost,
                    authCredentials = authCredentials,
                    responseState = responseState,
                    onUpdate = { authCredentials = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !responseState.isLoading,
                        colors = ButtonDefaults.errorTextButtonColors()
                    ) { Label(text = "Cancel") }
                    TextButton(
                        onClick = { onLogin(authCredentials) },
                        enabled = authCredentials.isValid() && !responseState.isLoading,
                        colors = ButtonDefaults.primaryTextButtonColors()
                    ) { Label(text = "Save") }
                }
            }
        }
    }
}

@Composable
private fun Preview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        ServerLoginFormDialog(
            showHost = true,
            responseState = ServerLoginFormResponseState(
                errors = mapOf(
                    ErrorKey.HOST to "Invalid host",
                    ErrorKey.USERNAME to "Invalid username",
                    ErrorKey.PASSWORD to "Invalid password"
                ),
                isLoading = false
            ),
            onLogin = { },
            onDismiss = { },
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

@Composable
private fun Preview_AddUser(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        ServerLoginFormDialog(
            showHost = false,
            responseState = ServerLoginFormResponseState(
                errors = mapOf(
                    ErrorKey.HOST to "Invalid host",
                    ErrorKey.USERNAME to "Invalid username",
                    ErrorKey.PASSWORD to "Invalid password"
                ),
                isLoading = false
            ),
            onLogin = { },
            onDismiss = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_AddUser_Dark() {
    Preview_AddUser(true)
}

@Preview(showBackground = true)
@Composable
private fun Preview_AddUser_White() {
    Preview_AddUser(false)
}