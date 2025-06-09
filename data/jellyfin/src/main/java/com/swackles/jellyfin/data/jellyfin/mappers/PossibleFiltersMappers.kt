package com.swackles.jellyfin.data.jellyfin.mappers

import com.swackles.jellyfin.data.jellyfin.models.PossibleFilters
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.model.api.QueryFiltersLegacy

internal fun Response<QueryFiltersLegacy>.toPossibleFilters(): PossibleFilters =
    PossibleFilters(
        genres = content.genres.orEmpty(),
        tags = content.tags.orEmpty(),
        ratings = content.officialRatings.orEmpty(),
        years = content.years.orEmpty()
    )