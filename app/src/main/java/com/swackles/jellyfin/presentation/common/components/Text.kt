package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun H2(text: String) {
    BaseText(
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}
@Composable
fun H5(text: String) {
    BaseText(
        text = text,
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun P(modifier: Modifier = Modifier, text: String, isError: Boolean = false, align: TextAlign? = null) {
    BaseText(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        isError = isError,
        align = align,
        modifier = modifier)
}

@Composable
private fun BaseText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    isError: Boolean = false,
    align: TextAlign? = null,
    color: Color? = null
) {
    Text(
        modifier = modifier
            .padding(horizontal = TextOptions.FontHorizontalPadding),
        style = style,
        color = color ?: if (isError) MaterialTheme.colorScheme.error else Color.Unspecified,
        text = text,
        textAlign = align
    )
}

private object TextOptions {
    val FontHorizontalPadding = 5.dp
}