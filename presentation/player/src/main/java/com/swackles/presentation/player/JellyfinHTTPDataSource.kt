package com.swackles.presentation.player

import android.os.Build.MODEL
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource

@OptIn(UnstableApi::class)
internal class JellyfinHTTPDataSource(private val token: String, private val deviceId: String) : HttpDataSource.Factory {
    override fun createDataSource(): HttpDataSource {
        return DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(getRequestProperties())
            .createDataSource()
    }

    override fun setDefaultRequestProperties(defaultRequestProperties: MutableMap<String, String>): HttpDataSource.Factory {
        TODO("Not implemented, use constructor")
    }

    private fun getRequestProperties(): HashMap<String, String> = hashMapOf(
        JELLYFIN_AUTH_HEADER to getAuthHeaderValue()
    )

    private fun getAuthHeaderValue(): String {
        val values = listOf(
            "$JELLYFIN_AUTH_CLIENT_KEY=\"$JELLYFIN_AUTH_CLIENT_VALUE\"",
            "$JELLYFIN_AUTH_DEVICE_KEY=\"$MODEL\"",
            "$JELLYFIN_AUTH_DEVICE_ID_KEY=\"$deviceId\"",
            "$JELLYFIN_AUTH_VERSION_KEY=\"$JELLYFIN_AUTH_VERSION_VALUE\"",
            "$JELLYFIN_AUTH_TOKEN_Key=\"$token\"",
        )

        return "MediaBrowser " + values.joinToString(",")
    }

    private companion object {
        const val JELLYFIN_AUTH_CLIENT_KEY = "Client"
        const val JELLYFIN_AUTH_CLIENT_VALUE = "Jellyfin WIP app"
        const val JELLYFIN_AUTH_DEVICE_KEY = "Device"
        const val JELLYFIN_AUTH_DEVICE_ID_KEY = "DeviceId"
        const val JELLYFIN_AUTH_VERSION_KEY = "Version"
        const val JELLYFIN_AUTH_VERSION_VALUE = "0.1-ALPHA"
        const val JELLYFIN_AUTH_TOKEN_Key = "Token"

        const val JELLYFIN_AUTH_HEADER = "Authorization"
    }
}