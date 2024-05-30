package com.swackles.auth

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.PasswordOutlinedTextField

@Composable
internal fun ServerLoginInputs(
    showHost: Boolean,
    authCredentials: AuthCredentials,
    responseState: ServerLoginFormResponseState,
    onUpdate: (credentials: AuthCredentials) -> Unit
) {
    if (showHost) {
        OutlinedTextField(
            value = authCredentials.host ?: "",
            label = { P(text = "Host") },
            readOnly = responseState.isLoading,
            placeholder = { P(text = "https://localhost:8096") },
            onValueChange = { onUpdate(authCredentials.copy(host = it)) },
            isError = responseState.errors.containsKey(ErrorKey.HOST),
            supportingText = { P(text = responseState.errors[ErrorKey.HOST] ?: "") })
    }
    OutlinedTextField(
        value = authCredentials.username,
        label = { P(text = "Username") },
        readOnly = responseState.isLoading,
        onValueChange = { onUpdate(authCredentials.copy(username = it)) },
        isError = responseState.errors.containsKey(ErrorKey.USERNAME),
        supportingText = { P(text = responseState.errors[ErrorKey.USERNAME] ?: "") })
    PasswordOutlinedTextField(
        value = authCredentials.password,
        label = { P(text = "Password") },
        readOnly = responseState.isLoading,
        onValueChange = { onUpdate(authCredentials.copy(password = it)) },
        isError = responseState.errors.containsKey(ErrorKey.PASSWORD),
        supportingText = { P(text = responseState.errors[ErrorKey.PASSWORD] ?: "") })
}
