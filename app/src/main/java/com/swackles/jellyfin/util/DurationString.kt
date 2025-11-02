package com.swackles.jellyfin.util

import kotlin.math.roundToLong

fun durationString(runtimeTicks: Long): String {
    var minutes = (runtimeTicks / 600000000.0).roundToLong()
    val hours = kotlin.math.floor(minutes / 60.0).toInt()
    minutes -= hours * 60

    var durationString = ""

    if (hours > 0) durationString += "$hours hours"
    if (minutes > 0) durationString += "$minutes min"

    return durationString
}