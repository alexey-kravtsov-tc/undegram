package krafts.alex.tg

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.drinkless.td.libcore.telegram.TdApi
import org.junit.Assert
import org.junit.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ClientFlowProviderTest {

    private val handlerFlow = ResultHandlerFlow(BroadcastChannel(64))
    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun `emit 10 receive 10`() = runBlocking {
        val flow = handlerFlow
        repeat(10) { handlerFlow.onResult(TdApi.UpdateChatLastMessage()) }

        val list = flow.take(10).toList()
        Assert.assertEquals(10, list.size)
        Assert.assertTrue(list.all { it is TdApi.UpdateChatLastMessage })

    }

    @Test
    fun `parent class filter emit result`() = runBlockingTest {
        val flow = handlerFlow

        handlerFlow.onResult(TdApi.UpdateAuthorizationState())
        val list =
            flow.filterIsInstance<TdApi.UpdateAuthorizationState>()
                .toList()
        Assert.assertTrue(list.isNotEmpty())
    }

    @Test
    fun `fuck my life`() = runBlockingTest {
        val flow = handlerFlow
        val job1 = launch {
            flow.filterIsInstance<TdApi.UpdateAuthorizationState>()
                .collect {
                    println("beautiful")
                }
        }

        val launch = launch {
            flow.filterIsInstance<TdApi.UpdateChatLastMessage>()
                .collect {
                    println("beautiful2")
                }
        }
        val launch1 = launch {
            flow.filterIsInstance<TdApi.UpdateBasicGroup>()
                .collect {
                    println("beautiful3")
                }
        }
        repeat(10) { handlerFlow.onResult(TdApi.UpdateChatLastMessage()) }

        repeat(10) { handlerFlow.onResult(TdApi.UpdateBasicGroup()) }
        repeat(10) { handlerFlow.onResult(TdApi.UpdateChatLastMessage()) }
        repeat(10) { handlerFlow.onResult(TdApi.UpdateAuthorizationState()) }

        println("UpdateAuthorizationState")

        handlerFlow.channel.cancel()
        joinAll(job1, launch, launch1)

        }

    @Test
    fun `exception test`() = runBlockingTest {
        val test = "nahui"
        val flow = handlerFlow
        flow.onException(Exception(test))
        val job1 = launch {
            flow.filterIsInstance<TdApi.Error>()
                .collect {
                    Assert.assertEquals(test, it.message)
                }
        }
        handlerFlow.channel.cancel()
        job1.join()
    }
}