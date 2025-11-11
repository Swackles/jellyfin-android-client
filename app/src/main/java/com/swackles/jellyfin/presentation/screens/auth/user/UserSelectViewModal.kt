package com.swackles.jellyfin.presentation.screens.auth.user

import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.Server
import com.swackles.jellyfin.session.Session
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

    data class Select(val sessions: List<Session>): Step
}

@HiltViewModel
class UserSelectViewModal @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)
    private lateinit var server: Server

    fun initialize(serverId: UUID) = viewModelScope.launch {
        server = sessionManager.findServer(serverId)!!
        val sessions = sessionManager.getSessions(server)

        setState { copy(step = Step.Select(sessions = sessions)) }
    }

    fun selectUser(sessionId: UUID) = viewModelScope.launch {
        val sessions = (state.value.step as Step.Select).sessions
        setState { copy(step = Step.Loading) }

        val session = sessions.first { it.id == sessionId }
        sessionManager.login(LoginCredentials.ExistingSession(session = session))
    }


    fun addUser() {
        setState { copy(step = Step.NavigateTo(route = LoginScreenDestination(server = server))) }
    }
}