package com.swackles.jellyfin.presentation.player.overlays

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.common.Overlay
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Composable fun LanguageAndSubtitlesOverlay(activeAudio: Int, audios: List<String>, activeSubtitle: Int, subtitles: List<String>) {
    Overlay(true) {
        Row(modifier = Modifier.fillMaxSize()) {
            SelectColumn(active = activeAudio, elements = audios)
            SelectColumn(active = activeSubtitle, elements = subtitles)
        }

    }
}

@Composable
private fun SelectColumn(active: Int, elements: List<String>) {
    Column(modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier) {
            elements.forEach {
                SelectChip(active = elements.indexOf(it) == active, label = it)
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectChip(active: Boolean, label: String) {
    FilterChip(
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Done,
                contentDescription = "hide_password"
            )
        },
        selected = active,
        onClick = { /*TODO*/ },
        label = { P(text = label) },
        border = FilterChipDefaults.filterChipBorder(borderWidth = 0.dp, selectedBorderWidth = 0.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            iconColor = Color.White,
            containerColor = Color.Transparent,
        )
    )
}

@Composable
@Preview(showBackground = true, widthDp = 780, heightDp = 360)
private fun PreviewOverlay() {
    JellyfinTheme(true) {
        LanguageAndSubtitlesOverlay(
            activeAudio = 1,
            audios = listOf("Audio 1", "Audio 2", "Audio 3"),
            activeSubtitle = 1,
            subtitles = listOf("Subtitle 1", "Subtitle 2", "Subtitle 3"),
        )
    }
}