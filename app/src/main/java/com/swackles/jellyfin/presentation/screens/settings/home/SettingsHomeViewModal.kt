package com.swackles.jellyfin.presentation.screens.settings.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.session.AuthState
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.Session
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jellyfin.sdk.model.UUID
import javax.inject.Inject

data class UiState(
    val step: Step
): ViewState

sealed interface Step {
    data object Loading: Step
    data class Success(
        val sessions: List<Session>,
        val activeSession: Session
    ): Step
}

@HiltViewModel
class SettingsHomeViewModal @Inject constructor(
    private val sessionManager: SessionManager
): BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(step = Step.Loading)

    init {
        viewModelScope.launch {
            val state = sessionManager.authState.value as AuthState.Authenticated
            val sessions = sessionManager.getSessions()

            setState { copy(step = Step.Success(sessions = sessions, activeSession = state.session)) }
        }
    }

    fun switchSession(id: UUID) = viewModelScope.launch {
        val session = sessionManager.getSessions().find { it.id == id }

        if (session != null) {
            sessionManager.login(LoginCredentials.ExistingSession(session))

        } else Log.e("SettingsHomeViewModal", "Session with id \"$id\" not found")
    }

    fun logoutServer() = viewModelScope.launch {
        sessionManager.logoutActiveServer()
    }

    fun logoutSession() = viewModelScope.launch {
        sessionManager.logoutActiveSession()
    }
}
