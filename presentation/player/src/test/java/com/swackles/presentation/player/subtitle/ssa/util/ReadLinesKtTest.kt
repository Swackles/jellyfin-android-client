package com.swackles.presentation.player.subtitle.ssa.util

import androidx.media3.common.util.ParsableByteArray
import org.junit.Test

import org.junit.Assert.*

class ReadLinesKtTest {
    @Test
    fun readLines_shouldTerminateAtEmptyLine() {
        val byteArray = ParsableByteArray(this.javaClass.classLoader!!.getResource("base.ass").readBytes())
        val lines = mutableListOf<String>()

        readLines(byteArray) { lines.add(it) }

        assertArrayEquals(arrayOf("Line 1", "Line 2", "Line 3"), lines.toTypedArray())
    }
    @Test
    fun readLines_shouldNotTerminateAtEmptyLine() {
        val byteArray = ParsableByteArray(this.javaClass.classLoader!!.getResource("base.ass").readBytes())
        val lines = mutableListOf<String>()

        readLines(byteArray, false) { lines.add(it) }

        assertArrayEquals(arrayOf("Line 1", "Line 2", "Line 3", "", "Line 4", "Line 5"), lines.toTypedArray())
    }

    @Test
    fun readLines_shouldTerminateAtEndOfString() {
        val byteArray = ParsableByteArray(this.javaClass.classLoader!!.getResource("base.ass").readBytes())
        val lines = mutableListOf<String>()

        readLines(byteArray) {  }
        readLines(byteArray) { lines.add(it) }

        assertArrayEquals(arrayOf("Line 4", "Line 5"), lines.toTypedArray())
    }
}