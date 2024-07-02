package com.swackles.presentation.player.subtitle

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes.TEXT_SSA
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.DefaultSubtitleParserFactory
import androidx.media3.extractor.text.SubtitleParser
import com.swackles.presentation.player.subtitle.ssa.SsaParser

@OptIn(UnstableApi::class)
class JellyfinSubtitleParserFactory: SubtitleParser.Factory {
    private val defaultSubtitleParserFactory = DefaultSubtitleParserFactory()

    override fun supportsFormat(format: Format): Boolean =
        defaultSubtitleParserFactory.supportsFormat(format)

    override fun getCueReplacementBehavior(format: Format): Int =
        defaultSubtitleParserFactory.getCueReplacementBehavior(format)

    override fun create(format: Format): SubtitleParser {
        return when(format.sampleMimeType) {
            TEXT_SSA -> SsaParser(format.initializationData)
            else -> defaultSubtitleParserFactory.create(format)
        }
    }
}