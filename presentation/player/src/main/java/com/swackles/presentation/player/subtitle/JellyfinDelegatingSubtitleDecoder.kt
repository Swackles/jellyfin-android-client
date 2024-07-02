package com.swackles.presentation.player.subtitle

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.SimpleSubtitleDecoder
import androidx.media3.extractor.text.Subtitle
import androidx.media3.extractor.text.SubtitleParser

// This code is taken from exoplayers DelegatingSubtitleDecoder
@OptIn(UnstableApi::class)
class JellyfinDelegatingSubtitleDecoder(name: String, private val subtitleParser: SubtitleParser) : SimpleSubtitleDecoder(name) {
    override fun decode(data: ByteArray, length: Int, reset: Boolean): Subtitle {
        if (reset) {
            subtitleParser.reset()
        }
        return subtitleParser.parseToLegacySubtitle(data, 0, length)
    }
}