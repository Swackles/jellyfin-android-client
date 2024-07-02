package com.swackles.presentation.player

import android.content.Context
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.text.TextRenderer
import com.swackles.presentation.player.subtitle.JellyfinSubtitleDecoderFactory

@OptIn(UnstableApi::class)
class JellyfinRenderersFactory(context: Context): DefaultRenderersFactory(context) {
    override fun buildTextRenderers(
        context: Context,
        output: TextOutput,
        outputLooper: Looper,
        extensionRendererMode: @ExtensionRendererMode Int,
        out: ArrayList<Renderer>
    ) {
        out.add(TextRenderer(output, outputLooper, JellyfinSubtitleDecoderFactory()))
    }
}
