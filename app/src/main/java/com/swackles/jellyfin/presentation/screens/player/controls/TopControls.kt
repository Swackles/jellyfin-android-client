package com.swackles.jellyfin.presentation.screens.player.controls

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swackles.jellyfin.presentation.styles.Spacings

@Composable
fun BoxScope.TopControls(
    modifier: Modifier,
    goBack: () -> Unit
) {
    Row(
        modifier = modifier
            .align(Alignment.TopCenter)
            .padding(start = Spacings.Large, top = Spacings.Medium, end = Spacings.Large)
            .fillMaxWidth()
    ) {
        IconButton(modifier = Modifier.size(18.dp), onClick = goBack) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Go back",
                tint = Color.White
            )
        }
    }
}
