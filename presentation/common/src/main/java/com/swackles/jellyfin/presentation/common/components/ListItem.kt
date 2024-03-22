package com.swackles.jellyfin.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme


@Composable
fun ListItem(
    onClick: () -> Unit,
    heading: String,
    leadingIcon: ImageVector,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(heading) },
        leadingContent = {
            Icon(
                leadingIcon,
                contentDescription = heading,
            )
        }
    )
}


@Composable
private fun Preview(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        ListItem(
            onClick = {},
            heading = "Servers",
            leadingIcon = Icons.Filled.Dns
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