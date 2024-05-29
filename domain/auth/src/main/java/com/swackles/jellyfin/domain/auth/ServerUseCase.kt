package com.swackles.jellyfin.domain.auth

import com.swackles.jellyfin.data.room.server.ServerRepository
import javax.inject.Inject

open class ServerUseCase @Inject constructor(
    private val serverRepository: ServerRepository
    ) {
    open val servers = serverRepository.allServers()
}