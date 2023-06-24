package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun H2(text: String) {
    BaseText(text, TextOptions.H2FontSize)
}

@Composable
fun P(text: String, isError: Boolean = false) {
    BaseText(text, TextOptions.PFontSize, isError)
}

@Composable
private fun BaseText(text: String, fontSize: TextUnit, isError: Boolean = false) {
    val modifier = Modifier
        .padding(horizontal = TextOptions.FontHorizontalPadding)

    val color = if (isError) MaterialTheme.colorScheme.error
                else Color.Unspecified

    Text(
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        text = text
    )
}

private object TextOptions {
    val H2FontSize = 22.sp
    val PFontSize = 14.sp
    val FontHorizontalPadding = 5.dp
}