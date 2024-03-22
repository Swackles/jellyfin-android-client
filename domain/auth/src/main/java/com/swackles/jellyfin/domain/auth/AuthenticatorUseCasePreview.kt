package com.swackles.jellyfin.domain.auth

import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepositoryPreview
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.server.ServerRepositoryPreview
import com.swackles.jellyfin.data.room.user.UserRepositoryPreview

class AuthenticatorUseCasePreview: AuthenticatorUseCase(
    JellyfinRepositoryPreview(),
    ServerRepositoryPreview(),
    UserRepositoryPreview()
) {
    override fun getAuthenticatedUser() = User(
        id = 1,
        username = "username",
        password = "password"
    )
}