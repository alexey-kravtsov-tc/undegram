package krafts.alex.tg

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.Edit
import krafts.alex.tg.entity.User
import krafts.alex.tg.repo.EditRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.UsersRepository
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

@ExperimentalCoroutinesApi
class TgClient(context: Context) : TelegramFlow(), KodeinAware {

    override val kodein: Kodein by closestKodein(context)

    private val notificationCompat = NotificationCompat.Builder(context, "tg")

    private val notificationManager = NotificationManagerCompat.from(context)

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    private val authorizationState = TdApi.UpdateAuthorizationState()
        .map { it.authorizationState }
        .onEach {
            when (it) {
                is TdApi.AuthorizationStateWaitTdlibParameters -> {
                    launch { TdApi.SetTdlibParameters(parameters).launch() }
                }

                is TdApi.AuthorizationStateWaitEncryptionKey -> {
                    launch { TdApi.CheckDatabaseEncryptionKey().launch() }
                }
            }
        }

    val loginState: LiveData<AuthState?> = authorizationState.asLiveData().map {
        when (it) {
            is TdApi.AuthorizationStateWaitPhoneNumber -> EnterPhone
            is TdApi.AuthorizationStateReady -> AuthOk
            is TdApi.AuthorizationStateWaitCode -> EnterCode
            is TdApi.AuthorizationStateWaitPassword -> EnterPassword(it.passwordHint)

            else -> null
        }
    }

    val haveAuthorization: Boolean get() = loginState.value == AuthOk

    suspend fun sendAuthPhone(phone: String) =
        TdApi.SetAuthenticationPhoneNumber(phone, null).expect<TdApi.Ok>()

    suspend fun sendAuthCode(code: String) =
        TdApi.CheckAuthenticationCode(code).expect<TdApi.Ok>()

    suspend fun sendAuthPassword(password: String) =
        TdApi.CheckAuthenticationPassword(password).expect<TdApi.Ok>()

    val userStatusFlow = TdApi.UpdateUserStatus().flow()

    val updateNewChatFlow = TdApi.UpdateNewChat().flow()
        .mapNotNull {
            Chat.fromTg(it.chat)
        }.onEach {
            if (it.photoBig?.downloaded == false)
                TdApi.DownloadFile(it.photoBig.fileId, 32, 0, 0, true).launch()
        }

    fun registerFirebaseNotifications(token: String) {
            TdApi.RegisterDevice(
                TdApi.DeviceTokenFirebaseCloudMessaging(token, false), null
            )
    }

    fun getChatInfo(chatId: Long) {
//        sendClient(TdApi.GetChat(chatId))
    }

    fun getUserInfo(userId: Int) {
//        sendClient(TdApi.GetUser(userId))
    }

    fun loadImage(id: Int) {
//        sendClient(TdApi.DownloadFile(id, 32, 0, 0, false))
    }

    private val messages = MessagesRepository(context)
    private val users = UsersRepository(context)
    private val messageEdits = EditRepository(context)

    private fun createClient(): Client = Client.create(Client.ResultHandler {
        //Log.e("--------result handled", it.toString())

        when (it) {
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
                    TdApi.DownloadFile(user.photoBig.fileId, 32, 0, 0, true)
            }

            is TdApi.UpdateUserChatAction -> {
                Log.e("-action", it.action.javaClass.simpleName)
            }

            is TdApi.UpdateFile -> {
                users.updateImage(it.file)
//                chats.updateImage(it.file)
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

/*
    private fun sendClient(query: TdApi.Function) {
        client.send(query) {
            when (it) {
                is TdApi.Error -> {
                    Log.e(this.toString(), it.message)
                }

                is TdApi.Ok -> {
                    //TODO HANDLE OK RESPONSE FOR AuthorizationStateWaitEncryptionKey
                }

                is TdApi.File -> {
                    if (it.local.isDownloadingCompleted) {
                        users.updateImage(it)
//                        chats.updateImage(it)
                    }
                }

                is TdApi.User -> {
                    val user = User.fromTg(it)
                    users.add(user)
                    if (user.photoBig?.downloaded == false)
                        sendClient(TdApi.DownloadFile(user.photoBig.fileId, 32, 0, 0, true))
                }

                else -> print("Receive wrong response from TDLib")
            }
        }
    }
*/

    data class Vpn(
        val ip: String,
        val port: Int,
        val username: String,
        val password: String
    )

    suspend fun addProxy(vpn: Vpn) {
        //TODO: use vpn if needed
        TdApi.AddProxy(
            vpn.ip,
            vpn.port,
            true,
            TdApi.ProxyTypeSocks5(vpn.username, vpn.password)
        ).launch()
    }

    companion object {
        private val parameters = TdApi.TdlibParameters().apply {
            databaseDirectory = "/data/user/0/krafts.alex.backupgram.app/files/tdlib"
            useMessageDatabase = false
            useSecretChats = false
            apiId = BuildConfig.apiId
            apiHash = BuildConfig.apiHash
            useFileDatabase = true
            systemLanguageCode = "en"
            deviceModel = "Desktop"
            systemVersion = "Undegram"
            applicationVersion = "1.0"
            enableStorageOptimizer = true
        }

    }
}