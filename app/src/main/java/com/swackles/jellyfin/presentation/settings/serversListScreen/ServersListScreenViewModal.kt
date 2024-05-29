package com.swackles.jellyfin.presentation.settings.serversListScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.data.room.models.Server
import com.swackles.jellyfin.domain.auth.AuthenticatorUseCase
import com.swackles.jellyfin.domain.auth.AuthenticatorUseCasePreview
import com.swackles.jellyfin.domain.auth.ServerUseCase
import com.swackles.jellyfin.domain.auth.ServerUseCasePreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ServersListScreenViewModal @Inject constructor(
    private val authenticatorUseCase: AuthenticatorUseCase,
    private val serverUseCase: ServerUseCase,
) : ViewModel() {
    open val servers = serverUseCase.servers
    open val activeServerId = authenticatorUseCase.authenticatedUser.value?.serverId

    fun setActiveServer(serverId: Long, navigator: DestinationsNavigator) {
        viewModelScope.launch {
            authenticatorUseCase.loginLastUsedUser(serverId)
            navigator.navigateUp()
        }
    }
}

internal class SettingsViewModalPreview(activeServerId: Long, servers: List<Server>): ServersListScreenViewModal(
    AuthenticatorUseCasePreview(),
    ServerUseCasePreview()
) {
    override val servers = flow { emit(servers) }
    override val activeServerId = activeServerId
}