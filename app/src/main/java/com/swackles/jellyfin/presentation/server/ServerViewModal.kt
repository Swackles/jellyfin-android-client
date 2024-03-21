package com.swackles.jellyfin.presentation.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.data.models.AuthenticatorResponse
import com.swackles.jellyfin.data.repository.JellyfinRepositoryPreview
import com.swackles.jellyfin.data.repository.ServerRepositoryPreview
import com.swackles.jellyfin.data.repository.UserRepositoryPreview
import com.swackles.jellyfin.data.useCase.AuthenticatorUseCase
import com.swackles.jellyfin.presentation.destinations.DashboardScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ErrorKey {
    HOST,
    USERNAME,
    PASSWORD
}

data class Inputs(
    val host: String = "",
    val username: String = "",
    val password: String = ""
)

data class ServerUiState(
    val inputs: Inputs = Inputs(),
    val isValidInput: Boolean = false,
    val isLoading: Boolean = false,
    val isInitializing: Boolean = true,
    val errors: Map<ErrorKey, String> = emptyMap()
)


@HiltViewModel
open class ServerViewModal @Inject constructor(
    private val authenticatorUseCase: AuthenticatorUseCase
    ) : ViewModel() {
    private var _serverUiState by mutableStateOf(ServerUiState())

    open fun getState(): ServerUiState {
        return _serverUiState
    }

    fun init(navigator: DestinationsNavigator) {
        viewModelScope.launch {
            handleLoginResponse(authenticatorUseCase.loginLastUsedUser(), navigator)
            authenticatorUseCase.loginLastUsedUser()
            finishInitializing()
        }
    }

    fun updateState(inputs: Inputs) {
        _serverUiState = _serverUiState.copy(
            inputs = inputs,
            isValidInput = isValid(inputs)
        )
    }

    suspend fun saveServer(navigator: DestinationsNavigator) {
        setLoading(true)
        val inputs = _serverUiState.inputs

        handleLoginResponse(authenticatorUseCase.login(inputs.host, inputs.username, inputs.password), navigator)

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
        val errors = _serverUiState.errors
            .minus(key)
            .plus(Pair(key, msg))

        _serverUiState = _serverUiState.copy(errors = errors)
    }

    private fun setError(keys: List<ErrorKey>, msg: String) {
        val errors = _serverUiState.errors

        keys.forEach {
            errors
                .minus(it)
                .plus(Pair(it, msg))
        }

        _serverUiState = _serverUiState.copy(errors = errors)
    }

    private fun finishInitializing() {
        _serverUiState = _serverUiState.copy(isInitializing = false)
    }

    private fun setLoading(isLoading: Boolean) {
        _serverUiState = _serverUiState.copy(isLoading = isLoading)
    }

    private fun isValid(inputs: Inputs): Boolean {
        return inputs.host.isNotBlank() && inputs.username.isNotBlank()
    }
}

class PreviewServerViewModal constructor(
    private val _serverUiState: ServerUiState = ServerUiState()
) : ServerViewModal(AuthenticatorUseCase(JellyfinRepositoryPreview(), ServerRepositoryPreview(), UserRepositoryPreview())) {
    override fun getState(): ServerUiState {
        return _serverUiState
    }
}
