package com.swackles.jellyfin.presentation.modifiers

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.progressStatus(progress: Float, colorOne: Color = colorScheme.primary, colorTwo: Color = colorScheme.onSurfaceVariant) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { 5.dp.toPx() }

        Modifier.drawWithContent {
            drawContent()

            val width = size.width * progress
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = colorOne,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )

            drawLine(
                color = colorTwo.copy(.5f),
                start = Offset(x = width, y = height),
                end = Offset(x = size.width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)