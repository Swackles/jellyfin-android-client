package com.swackles.jellyfin.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.swackles.jellyfin.session.LoginCredentials
import com.swackles.jellyfin.session.SessionManager
import com.swackles.jellyfin.util.BaseViewModel
import com.swackles.jellyfin.util.ViewState
import com.swackles.libs.jellyfin.JellyfinClientErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val step: Step
): ViewState

data class AuthCredentials(
    val hostname: String = "",
    val username: String = "",
    val password: String = ""
)

enum class ErrorKey {
    HOST,
    USERNAME,
    PASSWORD
}

sealed interface Step {
    data object Loading: Step
    data class EnterCredentials(
        val credentials: AuthCredentials = AuthCredentials(),
        val errors: Map<ErrorKey, String> = emptyMap()
    ): Step
}


@HiltViewModel
class AuthViewModal @Inject constructor(
    private val sessionManager: SessionManager
) : BaseViewModel<UiState>() {
    override fun initialState(): UiState = UiState(Step.EnterCredentials())
    fun login() = viewModelScope.launch {
        Log.d("AuthViewModal", "Starting log in")

        if (state.value.step !is Step.EnterCredentials) throw RuntimeException("Auth view model has incorrect state ($state.value.step) for logging in")
        val credentials = (state.value.step as Step.EnterCredentials).credentials.toLoginCredentials()
        setStepLoading()

        try {
            sessionManager.login(credentials)

            Log.d("AuthViewModal", "Login success")
        } catch (err: JellyfinClientErrors) {
            Log.e("AuthViewModal", "Login Failed", err)
            when(err) {
                is JellyfinClientErrors.InvalidHostnameError -> setStepError(mapOf(ErrorKey.HOST to "Incorrect hostname"))
                is JellyfinClientErrors.UnauthorizedError -> setStepError(mapOf(
                    ErrorKey.USERNAME to "Incorrect username",
                    ErrorKey.PASSWORD to "Incorrect password"
                ))
                else -> throw err
            }
        }
    }

    fun updateCredentials(newCredentials: AuthCredentials) {
        if (state.value.step !is Step.EnterCredentials) throw RuntimeException("Auth view model has incorrect state ($state.value.step) for updating credential")

        setState { copy(step = Step.EnterCredentials(credentials = newCredentials)) }
    }


    private fun setStepLoading() = setState { copy(step = Step.Loading) }
    private fun setStepError(errors: Map<ErrorKey, String>) = setState { copy(step = Step.EnterCredentials(errors = errors)) }

    private fun AuthCredentials.toLoginCredentials() =
        LoginCredentials(
            hostname = this.hostname,
            username = this.username,
            password = this.password
        )
}
