package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.presentation.common.components.P


@Composable
fun DetailTab(media: DetailMedia) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DetailKeyValue("Duration", media.getDurationString())
        DetailKeyValue("Genres", media.genres.joinToString("\n"))
        DetailKeyValue("Rating", media.rating)
        DetailKeyValue("Directors", media.directors.joinToString("\n"))
        DetailKeyValue("Writers", media.writers.joinToString("\n"))
        DetailKeyValue("Producers", media.producers.joinToString("\n"))
        DetailKeyValue("Actors", media.actors.joinToString("\n"))
        DetailKeyValue("Audios", media.audios.joinToString("\n"))
        DetailKeyValue("Subtitles", media.subtitles.joinToString("\n"))
    }
}

@Composable
fun DetailKeyValue(key: String, value: String?) {
    if (value == null) return

    Column(modifier = Modifier.fillMaxWidth()) {
        P("$key:")
        P("$value")
        Spacer(modifier = Modifier.size(20.dp))
    }
}
