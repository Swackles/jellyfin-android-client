package com.swackles.jellyfin.presentation.common.colors


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


@Composable
fun ButtonDefaults.primaryButtonColors() = buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary
)

@Composable
fun ButtonDefaults.primaryTextButtonColors() = textButtonColors(
    contentColor = MaterialTheme.colorScheme.primary
)

@Composable
fun ButtonDefaults.errorTextButtonColors() = textButtonColors(
    contentColor = MaterialTheme.colorScheme.error
)

@Composable
fun ButtonDefaults.primaryButtonContentPadding() = PaddingValues(
    start = ButtonConstants.ButtonHorizontalPadding,
    top = ButtonConstants.ButtonVerticalPadding,
    end = ButtonConstants.ButtonHorizontalPadding,
    bottom = ButtonConstants.ButtonVerticalPadding
)

private object ButtonConstants {
    val ButtonHorizontalPadding = 24.dp
    val ButtonVerticalPadding = 12.dp
}