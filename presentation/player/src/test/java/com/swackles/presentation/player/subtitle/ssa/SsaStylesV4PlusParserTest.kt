package com.swackles.presentation.player.subtitle.ssa

import androidx.media3.common.util.ParsableByteArray
import com.swackles.presentation.player.subtitle.ssa.enums.SsaAlignment
import com.swackles.presentation.player.subtitle.ssa.enums.SsaBorderStyle
import com.swackles.presentation.player.subtitle.ssa.models.SsaStyle
import org.junit.Test

import org.junit.Assert.*

class SsaStylesV4PlusParserTest {
    private val parser = SsaStylesV4PlusParser()

    @Test
    fun parse_fullStyleSection() {
        parser.parse(getParsableByteArray("full.ass"))

        assertEquals(2, parser.styles.count())

        val codeStyle = SsaStyle(
            "Code",
            "monospace",
            20,
            "&H00B0B0B0",
            "&H00303030",
            "&H80000008",
            true,
            false,
            false,
            false,
            100,
            100,
            0f,
            0f,
            SsaBorderStyle.OUTLINE,
            1f,
            2f,
            SsaAlignment.TOP_LEFT,
            30,
            10,
            30,
            0
        )
        assertEquals(codeStyle, parser.styles.get("Code"))
        val explStyle = SsaStyle(
            "Expl",
            "Arial",
            28,
            "&H00FFB0B0",
            "&H00303030",
            "&H80000008",
            true,
            false,
            false,
            false,
            100,
            100,
            0f,
            0f,
            SsaBorderStyle.OUTLINE,
            1f,
            2f,
            SsaAlignment.TOP_LEFT,
            30,
            10,
            30,
            0
        )
        assertEquals(explStyle, parser.styles.get("Expl"))
    }

    @Test
    fun parse_alignments() {
        parser.parse(getParsableByteArray("alignments.ass"))

        assertEquals(9, parser.styles.count())

        assertEquals(SsaAlignment.BOTTOM_LEFT, parser.styles["A1"]!!.alignment)
        assertEquals(SsaAlignment.BOTTOM_CENTER, parser.styles["A2"]!!.alignment)
        assertEquals(SsaAlignment.BOTTOM_RIGHT, parser.styles["A3"]!!.alignment)
        assertEquals(SsaAlignment.MID_LEFT, parser.styles["A4"]!!.alignment)
        assertEquals(SsaAlignment.MID_CENTER, parser.styles["A5"]!!.alignment)
        assertEquals(SsaAlignment.MID_RIGHT, parser.styles["A6"]!!.alignment)
        assertEquals(SsaAlignment.TOP_LEFT, parser.styles["A7"]!!.alignment)
        assertEquals(SsaAlignment.TOP_CENTER, parser.styles["A8"]!!.alignment)
        assertEquals(SsaAlignment.TOP_RIGHT, parser.styles["A9"]!!.alignment)
    }

    @Test
    fun parse_decorations() {
        parser.parse(getParsableByteArray("decorations.ass"))

        assertEquals(4, parser.styles.count())

        var style = parser.styles["S1"]!!
        assertEquals(true, style.bold)
        assertEquals(false, style.italic)
        assertEquals(false, style.underline)
        assertEquals(false, style.strikeOut)

        style = parser.styles["S2"]!!
        assertEquals(false, style.bold)
        assertEquals(true, style.italic)
        assertEquals(false, style.underline)
        assertEquals(false, style.strikeOut)

        style = parser.styles["S3"]!!
        assertEquals(false, style.bold)
        assertEquals(false, style.italic)
        assertEquals(true, style.underline)
        assertEquals(false, style.strikeOut)

        style = parser.styles["S4"]!!
        assertEquals(false, style.bold)
        assertEquals(false, style.italic)
        assertEquals(false, style.underline)
        assertEquals(true, style.strikeOut)
    }

    private fun getParsableByteArray(file: String) =
        ParsableByteArray(this.javaClass.classLoader!!.getResource("subtitles/styles/$file").readBytes())
}