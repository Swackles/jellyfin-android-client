package com.swackles.jellyfin.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.components.PasswordOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddUserModal(
    isLoading: Boolean,
    onToggleVisibility: () -> Unit,
    onAdd: (username: String, password: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onToggleVisibility,
        properties = ModalBottomSheetProperties(
            securePolicy = SecureFlagPolicy.SecureOff,
            isFocusable = true,
            shouldDismissOnBackPress = false
        )

    ) {
        ConstraintLayout {
            val (submitButton, inputs) = createRefs()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(inputs) {
                        centerHorizontallyTo(parent)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = username,
                    label = { P(text = "Username") },
                    readOnly = isLoading,
                    onValueChange = { username = it },
                    // isError = state.errors.containsKey(ErrorKey.USERNAME),
                    // supportingText = { P(text = state.errors[ErrorKey.USERNAME] ?: "") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordOutlinedTextField(
                    value = password,
                    label = { P(text = "Password") },
                    readOnly = isLoading,
                    onValueChange = { password = it },
                    // isError = state.errors.containsKey(ErrorKey.PASSWORD),
                    // supportingText = { P(text = state.errors[ErrorKey.PASSWORD] ?: "") }
                )
            }
            Button(
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 64.dp)
                    .constrainAs(submitButton) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                        top.linkTo(inputs.bottom)
                    },
                enabled = !isLoading,
                onClick = { onAdd(username, password) }
            ) {
                P(text = "connect")
            }
        }
    }
}