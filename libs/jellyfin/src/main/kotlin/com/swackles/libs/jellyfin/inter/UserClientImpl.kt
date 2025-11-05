package com.swackles.libs.jellyfin.inter

import com.swackles.libs.jellyfin.JellyfinUser
import com.swackles.libs.jellyfin.UserClient
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.sessionApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.model.api.UserDto

internal class UserClientImpl(
    private val jellyfinClient: ApiClient
): UserClient {
    override suspend fun endSession() {
        jellyfinClient.sessionApi.reportSessionEnded()
    }

    override suspend fun currentUser(): JellyfinUser =
        jellyfinClient.userApi
            .getCurrentUser().content
            .toJellyfinUser(apiClient = jellyfinClient)

    private fun UserDto.toJellyfinUser(apiClient: ApiClient) =
        JellyfinUser(
            id = id,
            username = name!!,
            baseUrl = apiClient.baseUrl!!,
            serverName = serverName ?: "Unknown Name",
            token = apiClient.accessToken!!
        )
}