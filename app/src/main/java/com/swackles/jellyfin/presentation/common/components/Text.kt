package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun H2(text: String) {
    BaseText(
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun P(text: String, isError: Boolean = false, alignCenter: Boolean = false) {
    BaseText(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        isError = isError,
        alignCenter = alignCenter)
}

@Composable
private fun BaseText(text: String, style: TextStyle, isError: Boolean = false, alignCenter: Boolean = false) {
    val modifier = Modifier
        .padding(horizontal = TextOptions.FontHorizontalPadding)

    val color = if (isError) MaterialTheme.colorScheme.error
                else Color.Unspecified

    Text(
        modifier = modifier,
        style = style,
        color = color,
        text = text
    )
}

private object TextOptions {
    val FontHorizontalPadding = 5.dp
}