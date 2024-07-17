package com.swackles.presentation.player.subtitle.ssa.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class OutlineShadowSpan(
    private val borderWidth: Float,
    private val borderColor: Int,
    private val shadowDepth: Float,
    private val shadowColor: Int
): ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        p4: Paint.FontMetricsInt?
    ): Int = paint.measureText(text.toString().substring(start, end)).toInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val originTextColor = paint.color

        paint.apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
        canvas.drawText(text, start, end, x, y.toFloat(), paint)

        paint.apply {
            color = shadowColor
            style = Paint.Style.FILL
        }

        canvas.drawText(text, start, end, x + shadowDepth, y + shadowDepth, paint)

        paint.apply {
            color = originTextColor
            style = Paint.Style.FILL
        }
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}