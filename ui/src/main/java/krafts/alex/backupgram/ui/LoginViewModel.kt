package krafts.alex.backupgram.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import krafts.alex.tg.AuthState
import krafts.alex.tg.TgClient

class LoginViewModel(private val client: TgClient) : ViewModel() {

    private val state = MutableLiveData<LoginProgressState>()

    private lateinit var lastAuthState: AuthState

    val authState = MediatorLiveData<LoginProgressState>().also { mediator ->
        mediator.addSource(client.loginState) {
            lastAuthState = it
            mediator.postValue(LoginProgressState.Idle(it))
        }
        mediator.addSource(state) {
            mediator.postValue(it)
        }
    }

    fun sendPhone(phone: String) {
        launchStateAction {
            client.sendAuthPhone(phone)
        }
    }

    fun sendCode(code: String) {
        launchStateAction {
            client.sendAuthCode(code)
        }
    }

    fun sendPassword(password: String) {
        launchStateAction {
            client.sendAuthPassword(password)
        }
    }

    private fun launchStateAction(function: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO onError {
            state.postValue(
                LoginProgressState.Error(
                    authState = lastAuthState,
                    message = it.message ?: "unknown error"
                )
            )
        }) {
            state.postValue(LoginProgressState.Loading)
            function()
        }
    }
}