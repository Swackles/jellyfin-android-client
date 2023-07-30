package com.swackles.jellyfin.presentation.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.data.enums.JellyfinResponses
import com.swackles.jellyfin.data.repository.JellyfinRepository
import com.swackles.jellyfin.data.repository.JellyfinRepositoryPreview
import com.swackles.jellyfin.domain.models.Server
import com.swackles.jellyfin.domain.repository.ServerRepository
import com.swackles.jellyfin.domain.repository.ServerRepositoryPreview
import com.swackles.jellyfin.presentation.destinations.DashboardScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

enum class ErrorKey {
    HOST,
    USERNAME,
    PASSWORD
}

data class ServerUiState(
    val server: Server = Server(),
    val isValidInput: Boolean = false,
    val isLoading: Boolean = false,
    val isInitializing: Boolean = true,
    val errors: Map<ErrorKey, String> = emptyMap()
)


@HiltViewModel
open class ServerViewModal @Inject constructor(
    private val serverRepository: ServerRepository,
    private val jellyfinRepository: JellyfinRepository
    ) : ViewModel() {
    private var _serverUiState by mutableStateOf(ServerUiState())

    open fun getState(): ServerUiState {
        return _serverUiState
    }

    fun init(navigator: DestinationsNavigator) {
        println("init serverViewModal")
        serverRepository.getLastActiveServer().onEach {
            println("host: ${it?.host}; username ${it?.username}")
            if (it != null) {
                when(jellyfinRepository.login(it.host, it.username, it.password)) {
                    JellyfinResponses.SUCCESSFUL -> {
                        navigator.navigate(DashboardScreenDestination)
                    }
                    JellyfinResponses.UNAUTHORIZED_RESPONSE -> setError(ErrorKey.USERNAME, "Username or password is incorrect", isInitializing = false)
                    else -> setError(ErrorKey.HOST, "Unknown error occurred")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateState(server: Server) {
        _serverUiState = _serverUiState.copy(
            server = server,
            isValidInput = isValid(server)
        )
    }

    suspend fun saveServer(navigator: DestinationsNavigator) {
        setLoading(true)
        val server = _serverUiState.server

        when(jellyfinRepository.login(server.host, server.username, server.password)) {
            JellyfinResponses.SUCCESSFUL -> {
                serverRepository.addServer(server)
                navigator.navigate(DashboardScreenDestination)
            }
            JellyfinResponses.UNAUTHORIZED_RESPONSE -> setError(ErrorKey.USERNAME, "Username or password is incorrect")
            else -> setError(ErrorKey.HOST, "Unknown error occurred")
        }

        setLoading(false)
    }

    private fun setError(key: ErrorKey, msg: String, isInitializing: Boolean? = null) {
        val errors = _serverUiState.errors
            .minus(key)
            .plus(Pair(key, msg))

        _serverUiState = _serverUiState.copy(errors = errors, isInitializing = isInitializing ?: _serverUiState.isInitializing)
    }

    private fun setLoading(isLoading: Boolean) {
        _serverUiState = _serverUiState.copy(isLoading = isLoading)
    }

    private fun isValid(server: Server): Boolean {
        return server.host.isNotBlank() && server.username.isNotBlank()
    }
}

class PreviewServerViewModal constructor(
    private val _serverUiState: ServerUiState = ServerUiState()
) : ServerViewModal(ServerRepositoryPreview(), JellyfinRepositoryPreview()) {
    override fun getState(): ServerUiState {
        return _serverUiState
    }
}
