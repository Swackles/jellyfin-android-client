package com.swackles.jellyfin.presentation.player.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.models.MediaStreams
import com.swackles.jellyfin.presentation.common.Overlay
import com.swackles.jellyfin.presentation.common.components.H5
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Composable fun LanguageAndSubtitlesOverlay(showOverlay: Boolean, activeAudio: Int, audios: List<MediaStreams>, activeSubtitle: Int, subtitles: List<MediaStreams>) {
    Overlay(showOverlay) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 100.dp, vertical = 25.dp)) {
            SelectColumn(
                modifier = Modifier.weight(1f),
                title = "Audio",
                imageVector = Icons.Outlined.Translate,
                active = activeAudio,
                elements = audios
            )
            Spacer(modifier = Modifier.width(20.dp))
            SelectColumn(
                modifier = Modifier.weight(1f),
                title = "Subtitles",
                imageVector = Icons.Outlined.Subtitles,
                active = activeSubtitle,
                elements = subtitles
            )
        }

    }
}

@Composable
private fun SelectColumn(modifier: Modifier, title: String, imageVector: ImageVector, active: Int, elements: List<MediaStreams>) {
    Column(modifier = modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = imageVector, contentDescription = title, tint = MaterialTheme.colorScheme.outline)
            H5(text = title)
        }
        Column(modifier = Modifier) {
            elements.forEach {
                SelectChip(active = elements.indexOf(it) == active, label = it.title)
                if (elements.indexOf(it) != elements.lastIndex) Divider()
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
                contentDescription = "hide_password",
                tint = if (active) Color.White else Color.Transparent
            )
        },
        selected = active,
        onClick = { /*TODO*/ },
        label = { P(text = label) },
        border = FilterChipDefaults.filterChipBorder(borderColor = Color.Transparent, borderWidth = 0.dp, selectedBorderWidth = 0.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            selectedContainerColor = Color.Transparent,
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
            showOverlay = true,
            activeAudio = 1,
            audios = listOf(
                MediaStreams("Audio 1", 1),
                MediaStreams("Audio 2", 2),
                MediaStreams("Audio 3", 3),
            ),
            activeSubtitle = 1,
            subtitles = listOf(
                MediaStreams("Subtitle 1", 4),
                MediaStreams("Subtitle 2", 5),
                MediaStreams("Subtitle 3", 6)
            ),
        )
    }
}