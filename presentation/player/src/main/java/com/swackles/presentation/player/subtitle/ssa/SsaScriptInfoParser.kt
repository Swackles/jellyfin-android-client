package com.swackles.presentation.player.subtitle.ssa

import androidx.annotation.OptIn
import androidx.media3.common.util.ParsableByteArray
import androidx.media3.common.util.UnstableApi
import com.swackles.presentation.player.subtitle.ssa.util.readLines

@OptIn(UnstableApi::class)
class SsaScriptInfoParser {
    fun parse(parsableByteArray: ParsableByteArray) {
        // Just skip over it, provides no value to parsing
        readLines(parsableByteArray) { }
    }
}