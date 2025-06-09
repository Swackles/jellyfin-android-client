package com.swackles.jellyfin.presentation.server

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.auth.enums.ErrorKey
import com.swackles.auth.models.ServerLoginFormResponseState
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
    var authResponseState by mutableStateOf(ServerLoginFormResponseState())
        private set

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
        Log.d("ServerViewModal", "handleLoginResponse: $response")
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
        val errors = authResponseState.errors
            .minus(key)
            .plus(Pair(key, msg))

        authResponseState = authResponseState.copy(errors = errors)
    }

    private fun setError(keys: List<ErrorKey>, msg: String) {
        var errors = authResponseState.errors

        keys.forEach {
            errors = errors
                .minus(it)
                .plus(Pair(it, msg))
        }

        authResponseState = authResponseState.copy(errors = errors)
    }

    private fun finishInitializing() {
        _serverUiState = _serverUiState.copy(isInitializing = false)
    }

    private fun setLoading(isLoading: Boolean) {
        authResponseState = authResponseState.copy(isLoading = isLoading)
    }
}

class PreviewServerViewModal constructor(
    private val _serverUiState: ServerUiState = ServerUiState(),
) : ServerViewModal(AuthenticatorUseCase(
    ServerRepositoryPreview(),
    UserRepositoryPreview()
)) {
    override fun getState(): ServerUiState {
        return _serverUiState
    }
}
