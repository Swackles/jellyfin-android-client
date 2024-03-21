package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    value: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.FilterChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        selected = selected,
        onClick = onClick,
        label = { Text(value) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Selected"
                )
            }
        } else {
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Selected"
                )
            }
        }
    )
}

@Composable
private fun SelectedPreview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        FilterChip(
            value = "Test",
            selected = true,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedPreview_Dark() {
    SelectedPreview(true)
}

@Preview(showBackground = true)
@Composable
private fun SelectedPreview_White() {
    SelectedPreview(false)
}

@Composable
private fun NotSelectedPreview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        FilterChip(
            value = "Test",
            selected = false,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotSelectedPreview_Dark() {
    NotSelectedPreview(true)
}

@Preview(showBackground = true)
@Composable
private fun NotSelectedPreview_White() {
    NotSelectedPreview(false)
}