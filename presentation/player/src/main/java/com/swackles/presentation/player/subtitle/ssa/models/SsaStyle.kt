package com.swackles.presentation.player.subtitle.ssa.models

import android.graphics.Color
import com.google.common.primitives.Ints
import com.swackles.presentation.player.subtitle.ssa.enums.SsaAlignment
import com.swackles.presentation.player.subtitle.ssa.enums.SsaBorderStyle

@OptIn(ExperimentalStdlibApi::class)
class SsaStyle(
    val name: String,
    val fontName: String?,
    val fontSize: Int?,
    primaryColour: String?,
    val outlineColor: String?, // TODO: Implement
    backColour: String?,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean,
    val strikeOut: Boolean,
    val scaleX: Int, // TODO: Implement
    val scaleY: Int, // TODO: Implement
    val spacing: Float, // TODO: Implement
    val angle: Float, // TODO: Implement
    val borderStyle: SsaBorderStyle?, // TODO: Implement
    val outline: Float, // TODO: Implement
    val shadow: Float, // TODO: Implement
    val alignment: SsaAlignment?,
    val marginL: Int, // TODO: Implement
    val marginR: Int, // TODO: Implement
    val marginV: Int, // TODO: Implement
    val encoding: Int // TODO: Implement
) {
    val primaryColour: Int = parseColor(primaryColour)
    // It seems that when the back color is "0" then it's fully transparent. Doesn't make much sense, why this happens, might be missing something
    val backColour: Int = if (HIDDEN_COLOR == backColour) Color.TRANSPARENT else parseColor(backColour)

    private fun parseColor(color: String?): Int {
        if (color == null) return Color.TRANSPARENT

        val bgra = if (color.startsWith("&H")) color.substring(2).hexToLong() else color.hexToLong()

        val a = Ints.checkedCast(bgra shr 24 and 0xFF xor 0xFF)
        val b = Ints.checkedCast(bgra shr 16 and 0xFF)
        val g = Ints.checkedCast(bgra shr 8 and 0xFF)
        val r = Ints.checkedCast(bgra and 0xFF)
        return Color.argb(a, r, g, b)
    }

    private companion object {
        const val HIDDEN_COLOR = "&H00000000"
    }
}
