package com.swackles.jellyfin.data.extensions

import org.jellyfin.sdk.model.api.BaseItemDto
import kotlin.math.roundToInt

/**
 * @return the percentage of media played between 0 and 1
 */
fun BaseItemDto.playedPercentage(): Float {
    return if (this.userData?.played == true) 1f
        else this.userData?.playedPercentage?.toFloat() ?: .0f
}

fun BaseItemDto.isInProgress(): Boolean = this.playedPercentage() > 0

fun BaseItemDto.durationString(): String {
    if (this.runTimeTicks == null) return ""

    var minutes = (this.runTimeTicks!!.toLong() / 600000000.0).roundToInt()
    val hours = kotlin.math.floor(minutes / 60.0).toInt()
    minutes -= hours * 60

    var durationString = ""

    if (hours > 0) durationString += "$hours hours"
    if (minutes > 0) durationString += "$minutes min"

    return durationString
}