package krafts.alex.tg

sealed class AuthState
object EnterPhone : AuthState()
object EnterCode : AuthState()
data class EnterPassword(val hint: String) : AuthState()
object AuthOk : AuthState()