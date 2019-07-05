package krafts.alex.tg

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.Edit
import krafts.alex.tg.entity.User
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.EditRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepositoryImpl
import krafts.alex.tg.repo.UsersRepository
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

class TgClient(context: Context) {

    var client = createClient()

    var authorizationState: TdApi.AuthorizationState? = null

    var haveAuthorization: Boolean = false

    private var quiting: Boolean = false

    private val notificationCompat = NotificationCompat.Builder(context, "tg")

    private val notificationManager = NotificationManagerCompat.from(context)

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState?) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState
        }
        Log.e("--------state updated", authorizationState.toString())
        when (authorizationState) {
            is TdApi.AuthorizationStateWaitTdlibParameters-> {
                val parameters = TdApi.TdlibParameters().apply {
                    databaseDirectory = "/data/user/0/krafts.alex.backupgram.app/files/tdlib"
                    useMessageDatabase = false
                    useSecretChats = false
                    apiId = 327719
                    apiHash = "5a80c8bd8c05ffe941897a3faffe154a"
                    systemLanguageCode = "en"
                    deviceModel = "Desktop"
                    systemVersion = "Undegram"
                    applicationVersion = "1.0"
                    enableStorageOptimizer = true
                }
                sendClient(TdApi.SetTdlibParameters(parameters))
            }

            is TdApi.AuthorizationStateWaitEncryptionKey -> sendClient(TdApi.CheckDatabaseEncryptionKey())

            is TdApi.AuthorizationStateWaitPhoneNumber -> {
                TgEvent.publish(EnterPhone)
            }

            is TdApi.AuthorizationStateWaitCode -> {
                TgEvent.publish(EnterCode)
            }

            is TdApi.AuthorizationStateWaitPassword -> {
                TgEvent.publish(EnterPassword(authorizationState.passwordHint))
            }

            is TdApi.AuthorizationStateReady -> {
                TgEvent.publish(AuthOk)
                haveAuthorization = true
            }

            is TdApi.AuthorizationStateLoggingOut -> {
                haveAuthorization = false
                print("Logging out")
            }

            is TdApi.AuthorizationStateClosing -> {
                haveAuthorization = false
                print("Closing")
            }

            is TdApi.AuthorizationStateClosed -> {
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
    private val sessions = SessionRepositoryImpl(
        TgDataBase.getInstance(context).sessions(),
        TgDataBase.getInstance(context).users()
    )
    private val messageEdits = EditRepository(context)

    private fun createClient(): Client = Client.create(Client.ResultHandler {
        //Log.e("--------result handled", it.toString())

        when (it) {
            is TdApi.UpdateAuthorizationState ->
                onAuthorizationStateUpdated(it.authorizationState)
            is TdApi.UpdateNewMessage ->
                messages.add(it.message, it.message.content.text())

            is TdApi.UpdateMessageContent -> {
                messages.get(it.messageId)?.let { origin ->

                    messageEdits.add(Edit.fromMessage(origin))
                    messages.edit(it.messageId, it.newContent.text())

                    val before = origin.text
                    val after = it.newContent.text()

                    Log.e("~~~~~~~edited", "from $before to $after")
                }
            }

            is TdApi.UpdateDeleteMessages -> {
                if (it.isPermanent) {
                    for (id in it.messageIds) {
                        val message = messages.get(id)
                        Log.e("======removed", message?.text)
                        messages.delete(id)
                        val user = users.get(message?.senderId ?: 0)
                        if (message?.isPersonal() == true && (user?.notifyDelete == true
                                || preferences.getBoolean("notify_private", false))
                        ) {
                            val not = notificationCompat
                                .setSmallIcon(R.drawable.ic_delete)
                                .setContentTitle("${user?.firstName} deleted message")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentText(message.text).build()

                            notificationManager.notify(123, not)
                        }
                    }
                }
            }

            is TdApi.UpdateUser -> {
                val user = User.fromTg(it.user)
                users.add(user)
                if (user.photoBig?.downloaded == false)
                    sendClient(TdApi.DownloadFile(user.photoBig.fileId, 32, 0, 0, true))
            }

            is TdApi.UpdateNewChat -> {
                val chat = Chat.fromTg(it.chat)
                chats.add(chat)
                if (chat.photoBig?.downloaded == false)
                    sendClient(TdApi.DownloadFile(chat.photoBig.fileId, 32, 0, 0, true))
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

    fun TdApi.MessageContent.text(): String {
        val name = this.javaClass.simpleName
        return when (this) {
            is TdApi.MessageText -> this.text.text
            is TdApi.MessagePinMessage ->
                "[$name] ${messages.get(this.messageId)?.text}"
            is TdApi.MessagePhoto ->
                "[$name] ${this.caption?.text}"
            is TdApi.MessageVideo ->
                "[$name] ${this.caption?.text}"
            is TdApi.MessageAnimation ->
                "[$name] ${this.caption?.text}"
            else -> "[$name]"
        }
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