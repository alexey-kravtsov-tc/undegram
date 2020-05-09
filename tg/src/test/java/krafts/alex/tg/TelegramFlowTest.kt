package krafts.alex.tg

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.drinkless.td.libcore.telegram.TdApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class TelegramFlowTest {

    inner class TestFlow : TelegramFlow(flowProvider) {

        inline fun <reified T : TdApi.Update> getUpdateFlow(update: T) = update.asFlow()

        inline fun <reified UpdateType : TdApi.Update, ResultType> getMapFlow(
            update: UpdateType, crossinline block: suspend (UpdateType) -> ResultType
        ) = update.mapAsFlow(block)
    }

    private val testDispatcher = TestCoroutineDispatcher()

    @MockK
    lateinit var flowProvider: FlowProvider

    lateinit var telegramFlow: TestFlow

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        telegramFlow = TestFlow()
    }

    @Test
    fun `flow receive emitted update`() {
        val test = 42L

        testFlow {
            emit(TdApi.UpdateDeleteMessages().also {
                it.chatId = test
            })
        }
        runBlocking {
            telegramFlow.getUpdateFlow(TdApi.UpdateDeleteMessages()).collect {
                assertNotNull(it)
                assertEquals(it.chatId, test)
            }
        }
    }

    @Test
    fun `flow receive two different type emits`() {
        val test = 42L
        testFlow {
            emit(TdApi.UpdateChatLastMessage().also { it.chatId = test })
            emit(TdApi.UpdateDeleteMessages().also {
                it.chatId = test
            })
        }
        runBlocking {
            telegramFlow.getUpdateFlow(TdApi.UpdateDeleteMessages()).collect {
                assertNotNull(it)
                assertEquals(it.chatId, test)
            }
            telegramFlow.getUpdateFlow(TdApi.UpdateChatLastMessage()).collect {
                assertNotNull(it)
                assertEquals(it.chatId, test)
            }
        }
    }

    @Test
    fun `map receive id from single emit`() {
        val test = 42L

        testFlow {
            emit(TdApi.UpdateDeleteMessages().also {
                it.chatId = test
            })
        }
        runBlocking {
            telegramFlow.getMapFlow(TdApi.UpdateDeleteMessages()) { it.chatId }.collect {
                assertNotNull(it)
                assertEquals(it, test)
            }
        }
    }

    @Test
    fun `collection from one not blocking another flow collection`() {
        testFlow {
            emit(TdApi.UpdateChatLastMessage())
            emit(TdApi.UpdateDeleteMessages())
        }
    }

    private fun testFlow(block: suspend FlowCollector<TdApi.Object>.() -> Unit) {
        every {
            flowProvider.getFlow()
        } returns flow { block() }
    }
}