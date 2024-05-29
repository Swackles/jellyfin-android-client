package com.swackles.jellyfin.presentation.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
import com.swackles.jellyfin.data.jellyfin.repository.JellyfinRepositoryPreview
import com.swackles.jellyfin.data.room.server.ServerRepositoryPreview
import com.swackles.jellyfin.data.room.user.UserRepositoryPreview
import com.swackles.jellyfin.domain.auth.AuthenticatorUseCase
import com.swackles.jellyfin.domain.auth.models.AuthCredentials
import com.swackles.jellyfin.domain.auth.models.AuthenticatorResponse
import com.swackles.jellyfin.presentation.destinations.DashboardScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServerUiState(
    val isInitializing: Boolean = true
)


@HiltViewModel
open class ServerViewModal @Inject constructor(
    private val authenticatorUseCase: AuthenticatorUseCase
    ) : ViewModel() {
    private var _serverUiState by mutableStateOf(ServerUiState())
    private var _authResponseState by mutableStateOf(ServerLoginFormResponseState())

    open val authResponseState: ServerLoginFormResponseState = this._authResponseState

    open fun getState(): ServerUiState {
        return _serverUiState
    }

    fun init(navigator: DestinationsNavigator) {
        viewModelScope.launch {
            handleLoginResponse(authenticatorUseCase.loginLastUsedUser(), navigator)
            finishInitializing()
        }
    }

    suspend fun saveServer(credentials: AuthCredentials, navigator: DestinationsNavigator) {
        setLoading(true)

        handleLoginResponse(authenticatorUseCase.login(credentials), navigator)

        setLoading(false)
    }

    private fun handleLoginResponse(response: AuthenticatorResponse, navigator: DestinationsNavigator) {
        when(response) {
            AuthenticatorResponse.NO_USER -> { /* Do nothing */ }
            AuthenticatorResponse.SUCCESS -> {
                navigator.navigate(DashboardScreenDestination)
            }
            AuthenticatorResponse.INVALID_CREDENTIALS -> setError(listOf(ErrorKey.USERNAME, ErrorKey.PASSWORD), "Username or password is incorrect")
            AuthenticatorResponse.INVALID_URL -> setError(ErrorKey.HOST, "Url is invalid")
            else -> setError(ErrorKey.HOST, "Unknown error occurred")
        }
    }

    private fun setError(key: ErrorKey, msg: String) {
        val errors = _authResponseState.errors
            .minus(key)
            .plus(Pair(key, msg))

        _authResponseState = _authResponseState.copy(errors = errors)
    }

    private fun setError(keys: List<ErrorKey>, msg: String) {
        val errors = _authResponseState.errors

        keys.forEach {
            errors
                .minus(it)
                .plus(Pair(it, msg))
        }

        _authResponseState = _authResponseState.copy(errors = errors)
    }

    private fun finishInitializing() {
        _serverUiState = _serverUiState.copy(isInitializing = false)
    }

    private fun setLoading(isLoading: Boolean) {
        _authResponseState = _authResponseState.copy(isLoading = isLoading)
    }
}

class PreviewServerViewModal constructor(
    private val _serverUiState: ServerUiState = ServerUiState(),
    private val _authResponseState: ServerLoginFormResponseState = ServerLoginFormResponseState()
) : ServerViewModal(AuthenticatorUseCase(JellyfinRepositoryPreview(),
    ServerRepositoryPreview(),
    UserRepositoryPreview()
)) {
    override fun getState(): ServerUiState {
        return _serverUiState
    }
}
