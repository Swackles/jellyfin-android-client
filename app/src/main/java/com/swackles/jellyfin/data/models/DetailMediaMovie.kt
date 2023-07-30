package com.swackles.jellyfin.data.models

import androidx.compose.ui.text.style.TextAlign
import org.jellyfin.sdk.model.api.BaseItemDto

open class DetailMediaMovie(
    private val baseItem: BaseItemDto,
    similar: List<Media>,
    baseUrl: String
) : DetailMediaBase(
    baseItem,
    similar,
    emptyList(),
    baseUrl
) {
    override fun getPlayShortcutInfo(): PlayShortcutInfo {
        val playedPercentage = if (baseItem.userData?.played == true) 100.0f
            else baseItem.userData?.playedPercentage?.toFloat() ?: .0f

        return PlayShortcutInfo(
            progress = playedPercentage / 100,
            labels = listOf(DetailMediaBarLabels(this.getDurationString(), TextAlign.Right)),
            mediaId = this.id,
            startPosition = baseItem.userData?.playbackPositionTicks ?: 0,
            isInProgress =  (baseItem.userData?.playedPercentage ?: .0) > 0
        )
    }
}