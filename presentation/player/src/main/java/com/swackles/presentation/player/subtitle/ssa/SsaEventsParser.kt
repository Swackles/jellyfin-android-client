package com.swackles.presentation.player.subtitle.ssa

import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ScaleXSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.text.Cue
import androidx.media3.common.text.Cue.AnchorType
import androidx.media3.common.text.Cue.LINE_TYPE_FRACTION
import androidx.media3.common.util.Consumer
import androidx.media3.common.util.ParsableByteArray
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.CuesWithTiming
import com.swackles.presentation.player.subtitle.ssa.enums.SsaAlignment
import com.swackles.presentation.player.subtitle.ssa.models.SsaStyle
import com.swackles.presentation.player.subtitle.ssa.spans.OutlineShadowSpan
import com.swackles.presentation.player.subtitle.ssa.util.readLines


@OptIn(UnstableApi::class)
class SsaEventsParser {
    private val format = mutableListOf<String>()
    private val cuesWithTiming = mutableListOf<CuesWithTiming>()

    fun parse(parsableByteArray: ParsableByteArray, styles: Map<String, SsaStyle>, output: Consumer<CuesWithTiming>) {
        readLines(parsableByteArray) { line ->
            val (key, line) = line.split(": ")

            when (key) {
                "Format" -> parseFormat(line)
                "Dialogue" -> parseDialog(line, styles)
            }
        }

        cuesWithTiming.forEach { output.accept(it) }
    }

    private fun parseDialog(line: String, styles: Map<String, SsaStyle>) {
        var startTimeMs: Long? = null
        var endTimeMs: Long? = null
        var style: SsaStyle? = null
        var text: String? = null

        line.split(",").forEachIndexed { index, value ->
            if (format.count() <= index) text += value
            else {
                when(format[index]) {
                    "Start" -> startTimeMs = timeToMs(value)
                    "End" -> endTimeMs = timeToMs(value)
                    "Text" -> text = value
                    "Style" -> style = styles[value]
                }
            }
        }

        cuesWithTiming.add(
            CuesWithTiming(
                listOf(
                    createCue(text!!, style!!)
                ),
                startTimeMs!!,
                endTimeMs!! - startTimeMs!!
            )
        )
    }

    private fun createCue(text: String, style: SsaStyle): Cue {
        val spannableText = SpannableString(text.replace("\\N", "\n"))
        val cue = Cue.Builder().setText(spannableText)

        spannableText.setSpan(
            ForegroundColorSpan(style.primaryColour),
            0,
            spannableText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableText.setSpan(
            BackgroundColorSpan(style.backgroundColor()),
            0,
            spannableText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (style.scaleX() != 1f) {
            spannableText.setSpan(
                ScaleXSpan(style.scaleX()),
                0, spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (style.shouldRenderOutlineAndShadow()) {
            spannableText.setSpan(
                OutlineShadowSpan(
                    style.outlineWidth(), style.outlineColor(),
                    style.shadowDepth(), style.shadowColor()
                ),
                0, spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (style.fontSize != null) {
            //TODO: Replace static screen height
            cue.setTextSize(style.fontSize / 360f, Cue.TEXT_SIZE_TYPE_FRACTIONAL_IGNORE_PADDING)
        }
        if (style.bold && style.italic)
            spannableText.setSpan(
                StyleSpan(Typeface.BOLD_ITALIC),
                0,
                spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        else if (style.bold)
            spannableText.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        else if (style.italic)
            spannableText.setSpan(
                StyleSpan(Typeface.ITALIC),
                0,
                spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        if (style.underline)
            spannableText.setSpan(
                UnderlineSpan(),
                0,
                spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        if (style.strikeOut)
            spannableText.setSpan(
                StrikethroughSpan(),
                0,
                spannableText.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )

        cue.setTextAlignment(toTextAlignment(style.alignment))
            .setPositionAnchor(toPositionAnchor(style.alignment))
            .setLineAnchor(toLineAnchor(style.alignment))

        cue.setPosition(computeDefaultLineOrPosition(cue.getPositionAnchor()))
        cue.setLine(computeDefaultLineOrPosition(cue.getLineAnchor()), LINE_TYPE_FRACTION)

        return cue.build()
    }


    private fun computeDefaultLineOrPosition(anchor: @AnchorType Int): Float {
        return when (anchor) {
            Cue.ANCHOR_TYPE_START -> DEFAULT_MARGIN
            Cue.ANCHOR_TYPE_MIDDLE -> 0.5f
            Cue.ANCHOR_TYPE_END -> 1.0f - DEFAULT_MARGIN
            Cue.TYPE_UNSET -> Cue.DIMEN_UNSET
            else -> Cue.DIMEN_UNSET
        }
    }

    private fun timeToMs(time: String): Long {
        val times = time.split(":")

        var totalTime = ((times[0].toLong() * 60) + times[1].toLong()) * 60 * 1000 * 1000
        if (times.count() == 4) totalTime += times[2].toLong() * 100 + times[3].toLong()
        else {
            val (seconds, milliseconds) = times[2].split(".")

            totalTime += (seconds.toLong() * 1000 + milliseconds.toLong()) * 1000
        }

        return totalTime
    }

    private fun toTextAlignment(alignment: SsaAlignment?): Layout.Alignment? {
        return when (alignment) {
            SsaAlignment.BOTTOM_LEFT, SsaAlignment.MID_LEFT, SsaAlignment.TOP_LEFT -> Layout.Alignment.ALIGN_NORMAL
            SsaAlignment.BOTTOM_CENTER, SsaAlignment.MID_CENTER, SsaAlignment.TOP_CENTER -> Layout.Alignment.ALIGN_CENTER
            SsaAlignment.BOTTOM_RIGHT, SsaAlignment.MID_RIGHT, SsaAlignment.TOP_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            null -> null
            else -> {
                Log.d("Player", "Unknown alignment: $alignment")
                null
            }
        }
    }

    private fun toLineAnchor(alignment: SsaAlignment?): @AnchorType Int {
        return when (alignment) {
            SsaAlignment.BOTTOM_LEFT, SsaAlignment.BOTTOM_CENTER, SsaAlignment.BOTTOM_RIGHT -> Cue.ANCHOR_TYPE_END
            SsaAlignment.MID_LEFT, SsaAlignment.MID_CENTER, SsaAlignment.MID_RIGHT -> Cue.ANCHOR_TYPE_MIDDLE
            SsaAlignment.TOP_LEFT, SsaAlignment.TOP_CENTER, SsaAlignment.TOP_RIGHT -> Cue.ANCHOR_TYPE_START
            null -> Cue.TYPE_UNSET
            else -> {
                Log.d("Player", "Unknown alignment: $alignment")
                Cue.TYPE_UNSET
            }
        }
    }

    private fun toPositionAnchor(alignment: SsaAlignment?): @AnchorType Int {
        return when (alignment) {
            SsaAlignment.BOTTOM_LEFT, SsaAlignment.MID_LEFT, SsaAlignment.TOP_LEFT -> Cue.ANCHOR_TYPE_START
            SsaAlignment.BOTTOM_CENTER, SsaAlignment.MID_CENTER, SsaAlignment.TOP_CENTER -> Cue.ANCHOR_TYPE_MIDDLE
            SsaAlignment.BOTTOM_RIGHT, SsaAlignment.MID_RIGHT, SsaAlignment.TOP_RIGHT -> Cue.ANCHOR_TYPE_END
            null -> Cue.TYPE_UNSET
            else -> {
                Log.d("Player", "Unknown alignment: $alignment")
                Cue.TYPE_UNSET
            }
        }
    }

    private fun parseFormat(line: String): Unit =
        line.split(",").forEach { format.add(it.trim()) }

    private companion object {
        const val DEFAULT_MARGIN = 0.05f
    }
}