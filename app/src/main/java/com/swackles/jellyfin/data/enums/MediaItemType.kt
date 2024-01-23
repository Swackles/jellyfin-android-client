package com.swackles.jellyfin.data.enums

import org.jellyfin.sdk.model.api.BaseItemKind

enum class MediaItemType(val baseItem: BaseItemKind) {
    MOVIE(BaseItemKind.MOVIE),
    SERIES(BaseItemKind.SERIES)
}