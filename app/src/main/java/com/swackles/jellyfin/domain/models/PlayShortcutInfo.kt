package com.swackles.jellyfin.domain.models

import java.util.UUID

data class PlayShortcutInfo(
    val progress: Float,
    val labels: List<DetailMediaBarLabels>,
    val mediaId: UUID,
    val startPosition: Long,
    val isInProgress: Boolean
)
