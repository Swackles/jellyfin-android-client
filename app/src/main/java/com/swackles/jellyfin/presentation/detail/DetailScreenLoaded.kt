package com.swackles.jellyfin.presentation.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.swackles.jellyfin.domain.models.DetailMedia
import com.swackles.jellyfin.presentation.common.components.P
import com.swackles.jellyfin.presentation.destinations.DetailScreenDestination
import com.swackles.jellyfin.presentation.detail.components.BannerImage
import com.swackles.jellyfin.presentation.detail.components.LogoImage
import com.swackles.jellyfin.presentation.detail.tabs.DetailScreenTabs

@Composable
fun DetailScreenLoaded(media: DetailMedia, navigator: DestinationsNavigator) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
            .verticalScroll(scrollState),
        ) {
            BannerImage(media = media, scrollState = scrollState)
            Spacer(modifier = Modifier.size(5.dp))
            Column(
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = DetailScreenLoadedProps.containerPadding)

            ) {
                LogoImage(media = media)
                Spacer(modifier = Modifier.size(5.dp))
                InfoRow(media.getInfo())
                Spacer(modifier = Modifier.size(5.dp))
                Button(
                    shape = RoundedCornerShape(DetailScreenLoadedProps.buttonBorderRadius),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "play arrow")
                    P("PLAY")
                }
                Spacer(modifier = Modifier.size(5.dp))
                if (media.overview != null) {
                    P(media.overview)
                    Spacer(modifier = Modifier.size(5.dp))
                }
                DetailScreenTabs(media, navigateToMediaView = { navigator.navigate(DetailScreenDestination(it)) })
            }
        }
    }
}

@Composable
private fun InfoRow(labels: List<String?>) {
    val color = MaterialTheme.colorScheme.outlineVariant

    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Center
    ) {
        labels.map {
            if (it === null) {
                Canvas(
                    modifier = Modifier.size(DetailScreenLoadedProps.dividerCircleSize),
                    onDraw = { drawCircle(color = color) }
                )
            } else {
                P(text = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetailScreenLoaded() {
    DetailScreenLoaded(DetailMedia.preview(), navigator = EmptyDestinationsNavigator)
}

private object DetailScreenLoadedProps {
    val dividerCircleSize = 5.dp
    val containerPadding = 10.dp
    val buttonBorderRadius = 5.dp
}