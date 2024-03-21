package com.swackles.jellyfin.presentation.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaBase
import com.swackles.jellyfin.data.jellyfin.models.DetailMediaMoviePreview
import com.swackles.jellyfin.presentation.common.components.H5
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.ui.theme.JellyfinTheme

@Composable
fun DetailTab(media: DetailMediaBase) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (media.genres.isNotEmpty()) DetailKeyValue("Genres") {
            Column(modifier = Modifier.fillMaxWidth()) {
                media.genres.map { P(text = it) }
            }
        }
        if (media.rating != null) DetailKeyValue("Rating") {
            P(text = media.rating!!)
        }
        if (media.directors.isNotEmpty()) DetailKeyValue("Directors") {
            Column(modifier = Modifier.fillMaxWidth()) {
                media.directors.map { P(text = it) }
            }
        }
        if (media.producers.isNotEmpty()) DetailKeyValue("Producers") {
            Column(modifier = Modifier.fillMaxWidth()) {
                media.producers.map { P(text = it) }
            }
        }
        if (media.writers.isNotEmpty()) DetailKeyValue("Writers") {
            Column(modifier = Modifier.fillMaxWidth()) {
                media.writers.map { P(text = it) }
            }
        }
        if (media.actors.isNotEmpty()) DetailKeyValue("Actors") {
            Column(modifier = Modifier.fillMaxWidth()) {
                media.actors.map { P(text = it) }
            }
        }
    }
}

@Composable
fun DetailKeyValue(key: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        H5("$key:")
        Spacer(modifier = Modifier.size(6.dp))
        content()
        Spacer(modifier = Modifier.size(10.dp))
    }
}

@Composable
private fun PreviewDetailTab(isDarkTheme: Boolean) {
    JellyfinTheme(isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DetailTab(DetailMediaMoviePreview())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetailTab_Dark() {
    PreviewDetailTab(true)
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetailTab_White() {
    PreviewDetailTab(false)
}