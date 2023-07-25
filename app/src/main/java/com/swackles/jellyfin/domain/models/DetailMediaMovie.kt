package com.swackles.jellyfin.domain.models

import androidx.compose.ui.text.style.TextAlign
import org.jellyfin.sdk.model.api.BaseItemDto

open class DetailMediaMovie(
    baseItem: BaseItemDto,
    similar: List<Media>,
    baseUrl: String
) : DetailMediaBase(
    baseItem,
    similar,
    emptyList(),
    baseUrl
) {
    override fun getProgressBarLabels() = listOf(DetailMediaBarLabels(this.getDurationString(), TextAlign.Right))
}