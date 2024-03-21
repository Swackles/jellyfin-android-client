package com.swackles.jellyfin.presentation.common.colors

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.common.theme.JellyfinTheme

@Composable
fun AssistChipDefaults.primaryAssistChipColors() = assistChipColors(
    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
)

@Composable
fun AssistChipDefaults.primaryAssistChipBorder() = assistChipBorder(true, borderColor = Color.Transparent)

@Composable
fun AssistChipDefaults.variantAssistChipColors() = assistChipColors(
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContainerColor = Color.Transparent,
    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
)

@Composable
fun AssistChipDefaults.variantAssistChipBorder() = assistChipBorder(
    true,
    borderColor = Color.Transparent,
    disabledBorderColor = Color.Transparent
)

@Composable
private fun PrimaryAssistChip(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AssistChip(
                colors = AssistChipDefaults.primaryAssistChipColors(),
                border = AssistChipDefaults.primaryAssistChipBorder(),
                onClick = { },
                label = { P(text = "Lorem Ipsum") },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryAssistChip_Light() {
    PrimaryAssistChip(false)
}

@Preview(showBackground = true)
@Composable
private fun PrimaryAssistChip_Dark() {
    PrimaryAssistChip(true)
}

@Composable
private fun VariantAssistChip(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AssistChip(
                colors = AssistChipDefaults.variantAssistChipColors(),
                border = AssistChipDefaults.variantAssistChipBorder(),
                onClick = { },
                label = { P(text = "Lorem Ipsum") },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VariantAssistChip_Light() {
    VariantAssistChip(false)
}

@Preview(showBackground = true)
@Composable
private fun VariantAssistChip_Dark() {
    VariantAssistChip(true)
}

@Composable
private fun VariantAssistChipDisabled(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AssistChip(
                enabled = false,
                colors = AssistChipDefaults.variantAssistChipColors(),
                border = AssistChipDefaults.variantAssistChipBorder(),
                onClick = { },
                label = { P(text = "Lorem Ipsum") },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VariantAssistChipDisabled_Light() {
    VariantAssistChipDisabled(false)
}

@Preview(showBackground = true)
@Composable
private fun VariantAssistChipDisabled_Dark() {
    VariantAssistChipDisabled(true)
}