package krafts.alex.tg

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import krafts.alex.tg.entity.Chat
import org.drinkless.td.libcore.telegram.TdApi

@ExperimentalCoroutinesApi
class TgClient(collectors: TelegramCollectors) : TelegramFlow(),
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {


    private val authState = TdApi.UpdateAuthorizationState().mapAsFlow { it.authorizationState }

    init {
        authState
            .onEach {
                delay(1000)
                when (it) {
                    is TdApi.AuthorizationStateWaitTdlibParameters -> {
                        TdApi.SetTdlibParameters(parameters).launch()
                    }

                    is TdApi.AuthorizationStateWaitEncryptionKey -> {
                        TdApi.CheckDatabaseEncryptionKey().launch()
                    }
                }
            }.launchIn(this)
    }

    val loginState: LiveData<AuthState?> = authState.asLiveData()
        .map {
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

    val newMessageFlow = TdApi.UpdateNewMessage().asFlow()

    val userStatusFlow = TdApi.UpdateUserStatus().asFlow()

    val updateNewChatFlow = TdApi.UpdateNewChat().asFlow()
        .mapNotNull {
            Chat.fromTg(it.chat)
        }.onEach {
            if (it.photoBig?.downloaded == false)
                TdApi.DownloadFile(it.photoBig.fileId, 32, 0, 0, true).launch()
        }

    val updateMessageFlow = TdApi.UpdateMessageContent().asFlow().onEach {
        "updated $it".log()
    }

    val deleteMessageFlow = TdApi.UpdateDeleteMessages().asFlow().onEach {
        "deleted $it".log()
    }

    val userFlow = TdApi.UpdateUser().asFlow()

    suspend fun downloadFile(fileId: Int) =
        TdApi.DownloadFile(fileId, 32, 0, 0, true).async<TdApi.File>()

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