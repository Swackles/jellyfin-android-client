package com.swackles.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme


@Composable
fun ServerLoginForm(
    responseState: ServerLoginFormResponseState,
    onSave: (inputs: AuthCredentials) -> Unit
) {
    var authCredentials by remember { mutableStateOf(AuthCredentials()) }

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
                ServerLoginInputs(
                    showHost = true,
                    authCredentials = authCredentials,
                    responseState = responseState,
                    onUpdate = { authCredentials = it }
                )
            }
            Button(
                modifier = Modifier.constrainAs(submitButton) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                    top.linkTo(inputs.bottom)
                },
                enabled = authCredentials.isValid() && !responseState.isLoading,
                onClick = { onSave(authCredentials) }
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