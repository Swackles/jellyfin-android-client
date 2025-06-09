package com.swackles.jellyfin.data.jellyfin.util

import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo
import java.util.UUID

internal fun createJellyfin(): Jellyfin =
    org.jellyfin.sdk.createJellyfin {
        clientInfo = ClientInfo(
            name = "Jellyfin WIP app",
            version = "0.1-ALPHA"
        )
        deviceInfo = DeviceInfo(
            id = UUID.randomUUID().toString(),
            name = "Mobile Device"
        )
    }