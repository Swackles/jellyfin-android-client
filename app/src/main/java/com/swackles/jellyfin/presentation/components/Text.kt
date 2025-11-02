package com.swackles.jellyfin.presentation.components

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
fun SmallTitle(modifier: Modifier = Modifier, text: String) =
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall,
        text = text
    )

@Composable
fun MediumTitle(modifier: Modifier = Modifier, text: String) =
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        text = text
    )

@Composable
fun LargeTitle(modifier: Modifier = Modifier, text: String) =
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        text = text
    )


@Composable
fun MediumText(
    modifier: Modifier = Modifier,
    text: String,
    isError: Boolean = false,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    BaseText(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        isError = isError,
        align = align,
        modifier = modifier,
        maxLines = maxLines
    )
}

@Composable
fun MediumTextMuted(text: String) =
    BaseText(
        text = text,
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = Int.MAX_VALUE
    )

@Composable
private fun BaseText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    isError: Boolean = false,
    align: TextAlign? = null,
    color: Color? = null,
    maxLines: Int
) {
    Text(
        modifier = modifier
            .padding(horizontal = TextOptions.FontHorizontalPadding),
        style = style,
        color = color ?: if (isError) MaterialTheme.colorScheme.error else Color.Unspecified,
        text = text,
        textAlign = align,
        maxLines = maxLines
    )
}

private object TextOptions {
    val FontHorizontalPadding = 5.dp
}