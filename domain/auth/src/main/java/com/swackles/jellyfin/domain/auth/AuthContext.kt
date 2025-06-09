package com.swackles.jellyfin.domain.auth

import android.app.Application
import com.swackles.jellyfin.data.jellyfin.JellyfinClient

/**
 * Not sure if this is correct, let alone good way of doing this. But I need to store this state somehow and no idea how else to do it.
 */
class AuthContext : Application() {
    companion object {
        @Volatile
        private var jellyfinClient: JellyfinClient? = null

        fun setJellyfinClient(client: JellyfinClient) {
            jellyfinClient = client
        }

        fun clearJellyfinClient() {
            jellyfinClient = null
        }

        fun getJellyfinClient(): JellyfinClient {
            if (jellyfinClient == null) throw IllegalStateException("JellyfinClient is not initialized. Call setJellyfinClient() first.")

            return jellyfinClient as JellyfinClient
        }
    }
}