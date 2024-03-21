package com.swackles.jellyfin.data.jellyfin.enums

import org.jellyfin.sdk.model.api.BaseItemKind

enum class MediaItemType(val baseItem: BaseItemKind) {
    MOVIE(BaseItemKind.MOVIE),
    SERIES(BaseItemKind.SERIES)
}