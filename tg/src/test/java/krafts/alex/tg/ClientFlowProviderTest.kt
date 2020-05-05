package krafts.alex.tg

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.drinkless.td.libcore.telegram.TdApi
import org.junit.Assert
import org.junit.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ClientFlowProviderTest {

    val provider = ClientFlowProvider()

    @Test
    fun `emit 10 receive 10`() = runBlocking {
        val flow = provider.channel.receiveAsFlow()
        repeat(10) { provider.resultHandler.onResult(TdApi.UpdateChatLastMessage()) }

        val list = flow.take(10).toList()
        Assert.assertEquals(10, list.size)
        Assert.assertTrue(list.all { it is TdApi.UpdateChatLastMessage })

    }
}