package krafts.alex.backupgram.ui

import krafts.alex.tg.AuthState

sealed class LoginProgressState() {
    interface HasState {
        val authState: AuthState
    }

    object Loading : LoginProgressState()
    data class Idle(override val authState: AuthState) : LoginProgressState(), HasState
    data class Error(
        override val authState: AuthState,
        val message: String
    ) : LoginProgressState(), HasState
}