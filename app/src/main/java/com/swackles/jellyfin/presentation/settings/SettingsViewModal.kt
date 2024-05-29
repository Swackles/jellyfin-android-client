package com.swackles.jellyfin.presentation.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.swackles.jellyfin.data.room.models.User
import com.swackles.jellyfin.data.room.user.UserRepository
import com.swackles.jellyfin.data.room.user.UserRepositoryPreview
import com.swackles.jellyfin.domain.auth.AuthenticatorUseCase
import com.swackles.jellyfin.domain.auth.AuthenticatorUseCasePreview
import com.swackles.jellyfin.domain.auth.models.AuthenticatorResponse
import com.swackles.jellyfin.presentation.destinations.ServerScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsViewModalState(
    val users: List<User> = emptyList(),
    val activeUser: User,
    val isLoading: Boolean = false,
    val isAddUserModalVisible: Boolean = false,
)

@HiltViewModel
open class SettingsViewModal @Inject constructor(
    private val authenticatorUseCase: AuthenticatorUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = mutableStateOf(
        SettingsViewModalState(
        activeUser = authenticatorUseCase.authenticatedUser.value!!
    )
    )
    open val state: State<SettingsViewModalState> = _state

    init {
        loadUsers()
    }

    fun toggleModalVisibility() {
        _state.value = _state.value.copy(isAddUserModalVisible = !_state.value.isAddUserModalVisible)
    }

    fun login(username: String, password: String) {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            val res = authenticatorUseCase.login(username, password)
            if (res == AuthenticatorResponse.SUCCESS) _state.value = _state.value.copy(activeUser = authenticatorUseCase.authenticatedUser.value!!)
        }
    }

    fun login(id: Long) {
        viewModelScope.launch {
            val res = authenticatorUseCase.login(id)
            if (res == AuthenticatorResponse.SUCCESS) _state.value = _state.value.copy(activeUser = authenticatorUseCase.authenticatedUser.value!!)
        }
    }

    suspend fun logoutUser(navigator: DestinationsNavigator) {
        when(authenticatorUseCase.logoutUser()) {
            AuthenticatorResponse.LOGOUT -> navigator.navigate(ServerScreenDestination)
            else -> { /* Do nothing */ }
        }
    }

    suspend fun logoutServer(navigator: DestinationsNavigator) {
        when(authenticatorUseCase.logoutServer()) {
            AuthenticatorResponse.LOGOUT -> navigator.navigate(ServerScreenDestination)
            else -> { /* Do nothing */ }
        }
    }

    private fun loadUsers() {
        userRepository.getAllUsersForServer(authenticatorUseCase.authenticatedUser.value?.serverId ?: 0).onEach {
            _state.value = _state.value.copy(users = it)
        }.launchIn(viewModelScope)
    }
}

internal class SettingsViewModalPreview(state: SettingsViewModalState): SettingsViewModal(
    AuthenticatorUseCasePreview(),
    UserRepositoryPreview()
) {
    override val state = mutableStateOf(state)
}

