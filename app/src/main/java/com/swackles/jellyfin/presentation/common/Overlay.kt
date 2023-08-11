package com.swackles.jellyfin.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope

@Composable
fun Overlay(showOverlay: Boolean, Content: @Composable () -> Unit) {
    if (!showOverlay) return

    Box(modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()
        .pointerInput(Unit) {
            forEachGesture {
                coroutineScope {
                    awaitPointerEventScope {
                        awaitFirstDown(requireUnconsumed = false).also { it.consume() }
                    }
                }

            }
        }
        .background(Color.Black.copy(.8f))) {
        Content()
    }
}