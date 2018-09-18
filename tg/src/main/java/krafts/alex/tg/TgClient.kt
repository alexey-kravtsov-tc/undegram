package krafts.alex.tg

import android.util.Log
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

class TgClient {

    var client = createClient()
    var authorizationState: TdApi.AuthorizationState? = null

    private var haveAuthorization: Boolean = false

    private var quiting: Boolean = false

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState?) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState
        }
        Log.e("--------state updated", authorizationState.toString())
        when (authorizationState?.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                val parameters = TdApi.TdlibParameters().apply {
                    databaseDirectory = "/data/user/0/krafts.alex.backupgram.app/files/tdlib"
                    useMessageDatabase = true
                    useSecretChats = true
                    apiId = 327719
                    apiHash = "5a80c8bd8c05ffe941897a3faffe154a"
                    systemLanguageCode = "en"
                    deviceModel = "Desktop"
                    systemVersion = "Unknown"
                    applicationVersion = "1.0"
                    enableStorageOptimizer = true
                }
                sendClient(TdApi.SetTdlibParameters(parameters))
            }
            TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> sendClient(TdApi.CheckDatabaseEncryptionKey())
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                TgEvent.publish(EnterPhone)
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                TgEvent.publish(EnterCode)
            }
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                TgEvent.publish(EnterPassword)
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                TgEvent.publish(AuthOk)
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                haveAuthorization = false
                print("Logging out")
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                haveAuthorization = false
                print("Closing")
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                print("Closed")
                if (!quiting) {
                    client = createClient() // recreate client after previous has closed
                }
            }
            else -> Log.e(this.toString(), "Unsupported authorization state: $authorizationState")
        }
    }

    fun sendPhone(phone: String) {
        sendClient(TdApi.SetAuthenticationPhoneNumber(phone, false, false))
    }

    fun sendCode(code: String) {
        sendClient(TdApi.CheckAuthenticationCode(code, "", ""))
    }

    fun sendPassword(password: String) {
        sendClient(TdApi.CheckAuthenticationPassword(password))
    }


    private fun promptString(msg: String): String? {
        Log.d("prompt", msg)
        TODO("action needed")
    }

    private fun print(msg: String) {
        Log.i("print", msg)
    }

    private val messages = ArrayList<TdApi.Message>()

    private fun createClient(): Client = Client.create(Client.ResultHandler {
        Log.e("--------result handled", it.toString())
        when (it) {
            is TdApi.UpdateAuthorizationState ->
                onAuthorizationStateUpdated(it.authorizationState)
            is TdApi.UpdateNewMessage -> messages.add(it.message)
            is TdApi.UpdateMessageContent -> {
                val before = messages
                        .find { msg -> msg.id == it.messageId}
                        ?.content?.text()
                val after = it.newContent.text()

                Log.e("~~~~~~~edited", "from $before to $after")

            }
            is TdApi.UpdateDeleteMessages -> {
                if (it.isPermanent) {
                    for (item in it.messageIds) {
                        Log.e("======removed", messages.find { it.id == item }?.content?.text())
                    }
                }
            }

        }
    }, Client.ExceptionHandler {
        Log.e(this.toString(), it.localizedMessage)
    }, null)

    private fun TdApi.MessageContent.text() : String {
        if (this is TdApi.MessageText) {
            return this.text.text
        }
        return ""
    }


    private fun sendClient(query: TdApi.Function) {
        client.send(query) {
            when (it.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    Log.e(this.toString(), (it as TdApi.Error).message)
                    this.onAuthorizationStateUpdated(null) // repeat last action
                }
                TdApi.Ok.CONSTRUCTOR -> {

                    //TODO HANDLE OK RESPONSE FOR AuthorizationStateWaitEncryptionKey
                }
                else -> print("Receive wrong response from TDLib")
            }
        }

    }


}