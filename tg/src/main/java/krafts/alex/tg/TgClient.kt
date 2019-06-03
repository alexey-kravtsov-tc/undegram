package krafts.alex.tg

import android.content.Context
import android.util.Log
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.User
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.UsersRepository
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class TgClient(context: Context) {

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

    fun getChatInfo(chatId: Long) {
        sendClient(TdApi.GetChat(chatId))
    }

    private fun print(msg: String) {
        Log.i("print", msg)
    }

    private val messages = MessagesRepository(context)
    private val users = UsersRepository(context)
    private val chats = ChatRepository(context)
    private val sessions = SessionRepository(context)

    private fun createClient(): Client = Client.create(Client.ResultHandler {
        //Log.e("--------result handled", it.toString())

        when (it) {
            is TdApi.UpdateAuthorizationState ->
                onAuthorizationStateUpdated(it.authorizationState)
            is TdApi.UpdateNewMessage ->
                messages.add(it.message)
            is TdApi.UpdateMessageContent -> {
                val origin = messages.get(it.messageId)

                val before = origin.text
                val after = it.newContent.text()

                Log.e("~~~~~~~edited", "from $before to $after")

            }
            is TdApi.UpdateDeleteMessages -> {
                if (it.isPermanent) {
                    for (id in it.messageIds) {
                        val message = messages.get(id)
                        Log.e("======removed", message.text)
                        messages.delete(id)
                    }
                }
            }
            is TdApi.UpdateUser -> {
                val user = User.fromTg(it.user)
                users.add(user)
                if (user.photoBig?.downloaded == false)
                    sendClient(TdApi.DownloadFile(user.photoBig.fileId, 32))

            }
            is TdApi.UpdateNewChat -> {
                val chat = Chat.fromTg(it.chat)
                chats.add(chat)
                if (chat.photoBig?.downloaded == false)
                    sendClient(TdApi.DownloadFile(chat.photoBig.fileId, 32))
            }

            is TdApi.UpdateUserStatus -> {
                if (it.status is TdApi.UserStatusOnline) {
                    sessions.updateSession(it)
                } else {
                    sessions.endSession(it.userId)
                }
            }

            is TdApi.UpdateFile -> {
                users.updateImage(it.file)
                chats.updateImage(it.file)
            }
        }
    }, Client.ExceptionHandler {
        Log.e(this.toString(), it.localizedMessage)
    }, null)


    private fun TdApi.MessageContent.text(): String {
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