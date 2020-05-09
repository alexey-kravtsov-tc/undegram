package krafts.alex.tg

import android.util.Log
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.drinkless.td.libcore.telegram.TdApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface FlowProvider {
    fun getFlow(): Flow<TdApi.Object>
    fun send(function: TdApi.Function, resultHandler: (TdApi.Object) -> Unit)
}

@ExperimentalCoroutinesApi
open class TelegramFlow(
    val flowProvider: FlowProvider = ClientFlowProvider()
) {

    val mainFlow: Flow<TdApi.Object> = flowProvider.getFlow()

    // transform

    inline fun <reified UpdateType : TdApi.Update> UpdateType.asFlow(): Flow<UpdateType> =
        mainFlow.filterIsInstance<UpdateType>()

    inline fun <reified UpdateType : TdApi.Update, ResultType>
        UpdateType.mapAsFlow(crossinline block: suspend (UpdateType) -> ResultType): Flow<ResultType> =
        mainFlow.transform { value ->
            if (value is UpdateType) emit(block(value))
        }


    //live data

    inline fun <reified UpdateType : TdApi.Update, LiveDataType>
        UpdateType.mapAsLiveData(crossinline block: (UpdateType) -> LiveDataType) =
        this.mapAsFlow { block(this) }.asLiveData()

//    fun <LiveDataType : TdApi.Object> Flow<LiveDataType>.asLiveData() =
//        liveData {
//            collect { emit(it) }
//        }

    //launchers

    suspend inline fun <reified ExpectedResult : TdApi.Object>
        TdApi.Function.expect(
        resultFunc: (ExpectedResult) -> Unit = {}
    ) = resultFunc(
        suspendCoroutine { cont ->
            flowProvider.send(this) {
                when (it) {
                    is ExpectedResult -> cont.resume(it)
                    is Error -> cont.resumeWithException(Exception(it.message))
                    else -> cont.resumeWithException(Exception("unexpected result $it"))
                }
            }
        }
    )

    suspend inline fun <reified ExpectedResult : TdApi.Object>
        TdApi.Function.async() : ExpectedResult = suspendCoroutine { cont ->
            flowProvider.send(this) {
                when (it) {
                    is ExpectedResult -> cont.resume(it)
                    is Error -> cont.resumeWithException(Exception(it.message))
                    else -> cont.resumeWithException(Exception("unexpected result $it"))
                }
            }
        }

    suspend inline fun TdApi.Function.launch() = suspendCoroutine<Boolean> { cont ->
        flowProvider.send(this) {
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
}

fun String.log() {
    Log.e("=========", this)
}