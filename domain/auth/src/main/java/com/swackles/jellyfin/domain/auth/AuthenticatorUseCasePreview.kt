package com.swackles.jellyfin.domain.auth

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepositoryPreview
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.server.ServerRepositoryPreview
import com.swackles.jellyfin.data.room.user.UserRepositoryPreview

class AuthenticatorUseCasePreview: AuthenticatorUseCase(
    JellyfinRepositoryPreview(),
    ServerRepositoryPreview(),
    UserRepositoryPreview()
) {
    override val authenticatedUser = MutableLiveData(User.preview())
}