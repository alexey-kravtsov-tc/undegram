package krafts.alex.tg

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

class ClientFlowProvider : FlowProvider {

    val channel = Channel<TdApi.Object>(Channel.UNLIMITED)

    lateinit var client: Client

    val resultHandler = Client.ResultHandler {

        if (!channel.isClosedForSend)
            channel.offer(it)

    }

    override fun getFlow(): Flow<TdApi.Object> {
        client = Client.create(
            resultHandler,
            Client.ExceptionHandler { throw Exception(it)},
            null
        )
        return channel.receiveAsFlow()
    }

    override fun send(function: TdApi.Function, resultHandler: (TdApi.Object) -> Unit) {
        client.send(function, resultHandler)
    }
}