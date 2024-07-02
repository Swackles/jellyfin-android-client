package com.swackles.presentation.player.subtitle

import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.text.SubtitleDecoderFactory
import androidx.media3.extractor.text.SubtitleDecoder
import androidx.media3.extractor.text.cea.Cea608Decoder
import androidx.media3.extractor.text.cea.Cea708Decoder

// This code is taken from exoplayers default implementation of SubtitleDecoderFactory
@UnstableApi
class JellyfinSubtitleDecoderFactory: SubtitleDecoderFactory {
    private val delegate = JellyfinSubtitleParserFactory()

    override fun supportsFormat(format: Format): Boolean {
        val mimeType = format.sampleMimeType
        return delegate.supportsFormat(format) || mimeType == MimeTypes.APPLICATION_CEA608 || mimeType == MimeTypes.APPLICATION_MP4CEA608 || mimeType == MimeTypes.APPLICATION_CEA708
    }

    override fun createDecoder(format: Format): SubtitleDecoder {
        val mimeType = format.sampleMimeType
        if (mimeType != null) {
            when (mimeType) {
                MimeTypes.APPLICATION_CEA608, MimeTypes.APPLICATION_MP4CEA608 -> return Cea608Decoder(
                    mimeType,
                    format.accessibilityChannel,
                    Cea608Decoder.MIN_DATA_CHANNEL_TIMEOUT_MS
                )

                MimeTypes.APPLICATION_CEA708 -> return Cea708Decoder(
                    format.accessibilityChannel,
                    format.initializationData
                )

                else -> {}
            }
        }
        if (delegate.supportsFormat(format)) {
            val subtitleParser = delegate.create(format)
            return JellyfinDelegatingSubtitleDecoder(subtitleParser.javaClass.simpleName + "Decoder", subtitleParser)
        }
        throw IllegalArgumentException(
            "Attempted to create decoder for unsupported MIME type: $mimeType"
        )
    }
}