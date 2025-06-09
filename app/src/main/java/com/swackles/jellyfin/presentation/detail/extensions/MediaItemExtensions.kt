package com.swackles.jellyfin.presentation.detail.extensions

import androidx.compose.ui.text.style.TextAlign
import com.swackles.jellyfin.data.jellyfin.models.MediaItem
import com.swackles.jellyfin.presentation.detail.DetailMediaBarLabel
import com.swackles.jellyfin.presentation.detail.PlayShortcutInfo
import kotlin.math.roundToInt

fun MediaItem.getInfo(): List<String> =
    when(this) {
        is MediaItem.Movie -> listOf(
            premiereDate.year.toString(),
            durationString(runTimeTicks),
            genres.first()
        )
        is MediaItem.Series -> listOf(
            premiereDate.year.toString(),
            genres.first()
        )
    }

fun MediaItem.getPlayShortcutInfo(): PlayShortcutInfo? =
    when(this) {
        is MediaItem.Movie -> PlayShortcutInfo(
            progress = playedPercentage,
            labels = listOf(DetailMediaBarLabel(durationString(runTimeTicks), TextAlign.Right)),
            mediaId = this.id,
            startPosition = playbackPositionTicks,
            isInProgress =  isInProgress()
        )
        is MediaItem.Series -> {
            if (episodes.isNotEmpty()) episodes.firstOrNull { it.isInProgress }.let {
                if (it == null) return null

                PlayShortcutInfo(
                    progress = it.playedPercentage ?: 0F,
                    labels = listOf(
                        DetailMediaBarLabel(
                            "S${it.season} E${it.episode} \"${it.title}\"",
                            TextAlign.Left
                        ),
                        DetailMediaBarLabel(durationString(it.runtimeTicks), TextAlign.Right),
                    ),
                    mediaId = it.id,
                    startPosition = it.playbackPositionTicks,
                    isInProgress = true
                )
            }
            else null
        }
    }

private fun durationString(runTimeTicks: Long): String {
    var minutes = (runTimeTicks / 600000000.0).roundToInt()
    val hours = kotlin.math.floor(minutes / 60.0).toInt()
    minutes -= hours * 60

    var durationString = ""

    if (hours > 0) durationString += "$hours hours"
    if (minutes > 0) durationString += "$minutes min"

    return durationString
}