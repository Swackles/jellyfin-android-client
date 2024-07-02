package com.swackles.presentation.player.subtitle.ssa

import androidx.annotation.OptIn
import androidx.media3.common.util.ParsableByteArray
import androidx.media3.common.util.UnstableApi
import com.swackles.presentation.player.subtitle.ssa.enums.SsaAlignment
import com.swackles.presentation.player.subtitle.ssa.enums.SsaBorderStyle
import com.swackles.presentation.player.subtitle.ssa.models.SsaStyle
import com.swackles.presentation.player.subtitle.ssa.util.readLines
import kotlin.RuntimeException

@OptIn(UnstableApi::class)
class SsaStylesV4PlusParser {
    private val format = mutableListOf<String>()
    private val _styles = mutableMapOf<String, SsaStyle>()

    val styles: Map<String, SsaStyle> = this._styles

    fun parse(parsableByteArray: ParsableByteArray) {
        readLines(parsableByteArray) { line ->
            val (key, values) = line.split(":")

            when (key) {
                "Format" -> parseFormat(values)
                "Style" -> parseStyles(values)
            }
        }
    }

    private fun parseStyles(line: String) {
        if (format.isEmpty()) throw RuntimeException("Format needs to be before styles")

        val values = line.split(",").map { it.trim() }
        val name = values[format.indexOf("Name")]

        _styles[name] = SsaStyle(
            name = name,
            fontName = getStringValue("Fontname", values),
            fontSize = getIntValue("Fontsize", values),
            primaryColour = getStringValue("PrimaryColour", values),
            outlineColor = getStringValue("OutlineColour", values),
            backColour = getStringValue("BackColour", values),
            bold = getBooleanValue("Bold", values),
            italic = getBooleanValue("Italic", values),
            underline = getBooleanValue("Underline", values),
            strikeOut = getBooleanValue("StrikeOut", values),
            scaleX = getIntValue("ScaleX", values) ?: 100,
            scaleY = getIntValue("ScaleY", values) ?: 100,
            spacing = getFloatValue("Spacing", values) ?: 0f,
            angle = getFloatValue("Angle", values) ?: 0f,
            borderStyle = parseBorderStyle("BorderStyle", values),
            outline = getFloatValue("Outline", values) ?: 0f,
            shadow = getFloatValue("Shadow", values) ?: 0f,
            alignment = parseAlignment("Alignment", values),
            marginL = getIntValue("MarginL", values) ?: 0,
            marginR = getIntValue("MarginR", values) ?: 0,
            marginV = getIntValue("MarginV", values) ?: 0,
            encoding = getIntValue("Encoding", values) ?: 0,
        )
    }

    private fun getStringValue(key: String, values: List<String>): String? {
        val index = format.indexOf(key)

        return if (index != -1) values[index] else null
    }

    private fun getFloatValue(key: String, values: List<String>): Float? = getStringValue(key, values)?.toFloat()

    private fun getIntValue(key: String, values: List<String>): Int? = getStringValue(key, values)?.toInt()

    private fun getBooleanValue(key: String, values: List<String>): Boolean = getStringValue(key, values) == "-1"

    private fun parseAlignment(key: String, values: List<String>): SsaAlignment {
        val index = format.indexOf(key)

        if (index == -1) return SsaAlignment.UNKNOWN

        return when(values[index]) {
            "1" -> SsaAlignment.BOTTOM_LEFT
            "2" -> SsaAlignment.BOTTOM_CENTER
            "3" -> SsaAlignment.BOTTOM_RIGHT
            "4" -> SsaAlignment.MID_LEFT
            "5" -> SsaAlignment.MID_CENTER
            "6" -> SsaAlignment.MID_RIGHT
            "7" -> SsaAlignment.TOP_LEFT
            "8" -> SsaAlignment.TOP_CENTER
            "9" -> SsaAlignment.TOP_RIGHT
            else -> throw RuntimeException("Unknown alignment value \"$values[index]\"")
        }
    }

    private fun parseBorderStyle(key: String, values: List<String>): SsaBorderStyle {
        val index = format.indexOf(key)

        if (index == -1) return SsaBorderStyle.UNKNOWN

        return when(values[index]) {
            "1" -> SsaBorderStyle.OUTLINE
            "3" -> SsaBorderStyle.OPAQUE_BOX
            else -> throw RuntimeException("Unknown border style value \"$values[index]\"")
        }
    }

    private fun parseFormat(line: String): Unit =
        line.split(",").forEach { format.add(it.trim()) }
}