package krafts.alex.tg

import android.util.Log
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
open class TelegramFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineScope by CoroutineScope(SupervisorJob() + dispatcher) {

    private val channel = Channel<TdApi.Object>(Channel.Factory.UNLIMITED)

    lateinit var client: Client

    @OptIn(FlowPreview::class)
    val mainFlow: Flow<TdApi.Object> by lazy {
        client = Client.create(Client.ResultHandler {

            it.toString().log()

            if (!channel.isClosedForSend)
                channel.offer(it)

        }, Client.ExceptionHandler {
            throw Exception(it)
        }, null
        )
        channel.receiveAsFlow()
    }

    inline fun <reified UpdateType : TdApi.Update, LiveDataType : TdApi.Object>
        UpdateType.mapLiveData(crossinline block: (UpdateType) -> LiveDataType) =
        liveData {
            mainFlow.transform { value: TdApi.Object ->
                if (value is UpdateType) emit(block(value))
            }.collect {
                emit(it)
            }
        }

    fun <LiveDataType : TdApi.Object> Flow<LiveDataType>.asLiveData() =
        liveData {
            collect {
                emit(it)
            }
        }

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

    suspend inline fun
        <reified T : TdApi.Update>
        T.collect(crossinline action: suspend (value: T) -> Unit) =
        mainFlow.transform { value: TdApi.Object ->
            if (value is T) emit(value as T)
        }.collect { action(it) }

    inline fun <reified UpdateType : TdApi.Update, ResultType>
        UpdateType.map(crossinline block: suspend (UpdateType) -> ResultType): Flow<ResultType> =
        mainFlow.transform { value: TdApi.Object ->
            if (value is UpdateType) emit(block(value))
        }

    inline fun <reified UpdateType : TdApi.Update> UpdateType.flow(): Flow<UpdateType> =
        mainFlow.filterIsInstance()
}

fun String.log() {
    Log.e("=========", this)
}