package com.swackles.presentation.player.subtitle.ssa.util

import androidx.annotation.OptIn
import androidx.media3.common.util.ParsableByteArray
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
fun readLines(parsableByteArray: ParsableByteArray, terminateAtBlankLine: Boolean = true, callback: (line: String) -> Unit) {
    var currentLine = parsableByteArray.readLine()

    while (currentLine != null && (if (terminateAtBlankLine) currentLine.isNotBlank() else true)) {
        callback(currentLine)
        currentLine = parsableByteArray.readLine()
    }
}
