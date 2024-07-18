package com.swackles.presentation.player.subtitle.ssa.models

import android.graphics.Color
import com.google.common.primitives.Ints
import com.swackles.presentation.player.subtitle.ssa.enums.SsaAlignment

@OptIn(ExperimentalStdlibApi::class)
class SsaStyle(
    val name: String,
    val fontName: String?,
    val fontSize: Int?,
    primaryColour: String?,
    private val outlineColor: String?,
    private val backColour: String?,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean,
    val strikeOut: Boolean,
    /*
     * 100 is default and from there it gets scaled up and down
     */
    private val scaleX: Int,
    val spacing: Float, // TODO: Implement
    val angle: Float, // TODO: Implement
    /*
     * 0 - None
     * 1 - Outline + Shadow. Specific size is determined, by the properties and color by outline color
     * 3 - Opaque box. Color is determined by backColor
     */
    private val borderStyle: Int,
    /*
     * Specifies the outline width, this can be between 0 and 4
     */
    private val outline: Int,
    /*
     * Specifies the outline width, this can be between
     */
    private val shadow: Int,
    val alignment: SsaAlignment?,
    val marginL: Int, // TODO: Implement
    val marginR: Int, // TODO: Implement
    val marginV: Int, // TODO: Implement
    val encoding: Int // TODO: Implement
) {
    val primaryColour: Int = parseColor(primaryColour)

    fun shouldRenderScaleX(): Boolean = scaleX != SCALE_DEFAULT.toInt()
    fun scaleX(): Float = scaleX / SCALE_DEFAULT

    private fun shouldRenderBackground(): Boolean = this.borderStyle == BORDER_STYLE_OPAQUE
    fun backgroundColor(): Int = if(shouldRenderBackground()) parseColor(backColour) else Color.TRANSPARENT

    fun shouldRenderOutlineAndShadow(): Boolean = this.borderStyle == BORDER_STYLE_OUTLINE
    /*
     * This isn't correct solution, it should return the dpi value based
     * on outline being the number of pixels in a border. But I'm unable
     * to figure out how can I get the screen density to here, so for now
     * this will have to do.
     *
     * Although, most likely does require some balancing based on how it looks on other screens.
     */
    fun outlineWidth(): Float = outline * BORDER_WIDTH_MULTIPLIER
    fun outlineColor(): Int = parseColor(outlineColor)
    /*
     * This isn't correct solution, it should return the dpi value based
     * on shadow being the number of pixels in a shadow. But I'm unable
     * to figure out how can I get the screen density to here, so for now
     * this will have to do.
     *
     * Although, most likely does require some balancing based on how it looks on other screens.
     */
    fun shadowDepth(): Float = shadow * BORDER_SHADOW_MULTIPLIER
    fun shadowColor(): Int = outlineColor()

    private fun parseColor(color: String?): Int {
        if (color == null) return Color.TRANSPARENT

        val bgra = if (color.startsWith("&H")) color.substring(2).hexToLong() else color.hexToLong()

        val a = Ints.checkedCast(bgra shr 24 and 0xFF xor 0xFF)
        val b = Ints.checkedCast(bgra shr 16 and 0xFF)
        val g = Ints.checkedCast(bgra shr 8 and 0xFF)
        val r = Ints.checkedCast(bgra and 0xFF)
        return Color.argb(a, r, g, b)
    }

    companion object {
        const val BORDER_STYLE_NONE = 0
        const val BORDER_STYLE_OUTLINE = 1
        const val BORDER_STYLE_OPAQUE = 3
        private const val BORDER_WIDTH_MULTIPLIER = 5f
        private const val BORDER_SHADOW_MULTIPLIER = 2.5f

        const val SCALE_DEFAULT = 100f

        const val HIDDEN_COLOR = "&H00000000"
    }
}
