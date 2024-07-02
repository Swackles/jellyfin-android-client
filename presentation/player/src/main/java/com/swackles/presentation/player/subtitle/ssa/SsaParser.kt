package com.swackles.presentation.player.subtitle.ssa

import androidx.media3.common.Format.CUE_REPLACEMENT_BEHAVIOR_MERGE
import androidx.media3.common.util.Consumer
import androidx.media3.common.util.Log
import androidx.media3.common.util.ParsableByteArray
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.CuesWithTiming
import androidx.media3.extractor.text.SubtitleParser
import com.swackles.presentation.player.subtitle.ssa.util.readLines

@UnstableApi
class SsaParser(initializationData: List<ByteArray> = emptyList()): SubtitleParser {
    private val parsableByteArray = ParsableByteArray()

    private val scriptInfoParser = SsaScriptInfoParser()
    private val v4PlusStylesParser = SsaStylesV4PlusParser()
    private val eventsParser = SsaEventsParser()

    init {
        readLines(parsableByteArray) { currentLine ->
            when (currentLine) {
                "[Script Info]" -> scriptInfoParser.parse(parsableByteArray)
                "[V4+ Styles]" -> v4PlusStylesParser.parse(parsableByteArray)
                else -> Log.e("Player", "$currentLine section is not supported")
            }
        }
    }

    override fun parse(
        data: ByteArray,
        offset: Int,
        length: Int,
        outputOptions: SubtitleParser.OutputOptions,
        output: Consumer<CuesWithTiming>
    ) {
        parsableByteArray.reset(data, offset + length)
        parsableByteArray.setPosition(offset)

        readLines(parsableByteArray, false) { currentLine ->
            when (currentLine) {
                "[Script Info]" -> scriptInfoParser.parse(parsableByteArray)
                "[V4+ Styles]" -> v4PlusStylesParser.parse(parsableByteArray)
                "[Events]" -> eventsParser.parse(parsableByteArray, v4PlusStylesParser.styles, output)
                else -> Log.e("Player", "$currentLine section is not supported")
            }
        }
    }

    override fun getCueReplacementBehavior(): Int = CUE_REPLACEMENT_BEHAVIOR_MERGE
}