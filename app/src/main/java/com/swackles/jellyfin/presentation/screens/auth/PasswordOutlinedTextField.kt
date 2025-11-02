package com.swackles.jellyfin.presentation.screens.auth

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.swackles.jellyfin.presentation.components.MediumText

@Composable
fun PasswordOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value,
        onValueChange,
        modifier,
        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon = { TrailingIcon(showPassword, { showPassword = !showPassword }) },
        prefix,
        suffix,
        supportingText,
        isError,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions,
        singleLine,
        maxLines,
        minLines,
        interactionSource,
        shape,
        colors,
    )
}

@Composable
private fun TrailingIcon(showPassword: Boolean, toggleShowPassword: () -> Unit) {
    if (showPassword) {
        IconButton(onClick = toggleShowPassword) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = "hide_password"
            )
        }
    } else {
        IconButton(
            onClick = toggleShowPassword) {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = "hide_password"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPasswordOutlinedTextField() {
    PasswordOutlinedTextField(
        value = "",
        onValueChange = {},
        label = { MediumText(text = "Password") })
}

@Preview(showBackground = true)
@Composable
private fun PreviewPasswordOutlinedTextFieldWithValue() {
    PasswordOutlinedTextField(
        value = "password",
        onValueChange = {},
        label = { MediumText(text = "Password") })
}

@Preview(showBackground = true)
@Composable
private fun PreviewPasswordOutlinedTextFieldIsErrored() {
    PasswordOutlinedTextField(
        value = "",
        onValueChange = {},
        label = { MediumText(text = "Password") },
        isError = true,
        supportingText = { MediumText(text = "Errored") })
}