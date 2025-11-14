package com.swackles.jellyfin.presentation.screens.auth.server

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UserSelectScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


data class UiState(
    val step: Step
): ViewState

sealed interface Step {
    data object Loading: Step

    data class NavigateTo(val route: Direction): Step

    data class Select(val servers: List<Server>): Step
}

@HiltViewModel
class ServerSelectViewModal @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)
    private lateinit var servers: List<Server>

    fun initialize() = viewModelScope.launch {
        Log.d("ServerSelectViewModal", "initialize")
        servers = sessionManager.getServers()

        if (servers.isEmpty()) return@launch newServer()

        setState { copy(step = Step.Select(servers = servers)) }
    }

    fun selectServer(serverId: UUID): Unit =
        setStepNavigateTo(UserSelectScreenDestination(serverId = serverId))


    fun newServer(): Unit =
        setStepNavigateTo(LoginScreenDestination())

    fun onNavigationHandled() {
        if (::servers.isInitialized) setState { copy(step = Step.Select(servers = servers)) }
        else  setState { initialState() }
    }


    private fun setStepNavigateTo(route: Direction): Unit =
        setState { copy(step = Step.NavigateTo(route = route)) }
}