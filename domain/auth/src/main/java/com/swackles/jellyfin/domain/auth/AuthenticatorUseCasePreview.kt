package com.swackles.jellyfin.domain.auth

import androidx.lifecycle.MutableLiveData
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.server.ServerRepositoryPreview
import com.swackles.jellyfin.data.room.user.UserRepositoryPreview

class AuthenticatorUseCasePreview: AuthenticatorUseCase(
    ServerRepositoryPreview(),
    UserRepositoryPreview()
) {
    override val authenticatedUser = MutableLiveData(User.preview())
}