package krafts.alex.tg

import android.util.Log
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
open class TelegramFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineScope by CoroutineScope(dispatcher) {

    lateinit var client: Client

    fun String.log() {
        Log.e("=========", this)
    }

    val mainFlow: Flow<TdApi.Object> =
        channelFlow<TdApi.Object> {
            client = Client.create(Client.ResultHandler{
                it.toString().log()
                //better
                channel.offer(it)
            },
            Client.ExceptionHandler{
                throw java.lang.Exception(it)
            }, null)
            awaitClose {
                "closed".log()
            }
        }.flowOn(dispatcher).buffer(10)

    inline fun <reified UpdateType : TdApi.Update, LiveDataType : TdApi.Object>
        UpdateType.mapLiveData(crossinline block: (UpdateType) -> LiveDataType) =
        liveData {
            mainFlow.transform { value: TdApi.Object ->
                if (value is UpdateType) emit(block(value))
            }.collect {
                emit(it)
            }
        }

    val state = TdApi.UpdateAuthorizationState().mapLiveData { it.authorizationState }

    fun <LiveDataType : TdApi.Object> Flow<LiveDataType>.asLiveData() =
        liveData {
            collect { emit(it)
        } }

    suspend inline fun <reified ExpectedResult : TdApi.Object>
        TdApi.Function.expect(
        resultFunc: (ExpectedResult) -> Unit = {}
    ) = resultFunc(
        suspendCoroutine { cont ->
            client.send(this) {
                when (it) {
                    is ExpectedResult -> cont.resume(it)
                    is Error -> cont.resumeWithException(Exception(it.message))
                    else -> cont.resumeWithException(Exception("unexpected result $it"))
                }
            }
        }
    )

    suspend fun sendAuthPhone2(phone: String) =
        TdApi.SetAuthenticationPhoneNumber(phone, false, false).expect<TdApi.Ok>()

    suspend inline fun TdApi.Function.launch(
    ) = suspendCoroutine<Boolean> { cont ->
        "send client $this".log()
        client.send(this) {
            when (it) {
                is TdApi.Ok -> {
                    "$this returns true".log()
                    cont.resume(true)
                }
                is TdApi.Error -> cont.resume(false)
                else -> cont.resumeWithException(Exception("unexpected result $it"))
            }
        }
    }

    suspend fun sendAuthPhone3(phone: String) =
        TdApi.SetAuthenticationPhoneNumber(phone, false, false).launch()

    suspend inline fun
        <reified T : TdApi.Update>
        T.collect(crossinline action: suspend (value: T) -> Unit)
        = mainFlow.transform { value: TdApi.Object ->
            if (value is T) emit(value as T)
        }.collect { action(it) }


     fun collectUsers() = launch {
         TdApi.UpdateUserStatus().collect {
             updateUsetStatus(it.status)
         }
     }

    fun updateUsetStatus(status: TdApi.UserStatus) {
        ///some work
    }

    suspend fun fee() {
        TdApi.UpdateAuthorizationState().collect {
            if (it.authorizationState is TdApi.AuthorizationStateWaitTdlibParameters) {
                val params = TdApi.TdlibParameters().apply {
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
                if (TdApi.SetTdlibParameters(params).launch()) {
                    //success
                }
            }
        }
        TdApi.UpdateNewMessage().map { it.message }.collect {
            if ((it.content as? TdApi.MessageText)?.text?.text == "test") {
                TdApi.DeleteChatMessagesFromUser(it.chatId, it.senderUserId)
            }
        }
    }

    inline fun <reified UpdateType : TdApi.Update, ResultType >
        UpdateType.map(crossinline block: suspend (UpdateType) -> ResultType): Flow<ResultType> =
            mainFlow.transform { value: TdApi.Object ->
                if (value is UpdateType) emit(block(value))
            }


}