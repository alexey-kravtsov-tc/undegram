package krafts.alex.tg.repo

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import krafts.alex.tg.TgClient
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.File
import org.drinkless.td.libcore.telegram.TdApi

class ChatRepository constructor(context: Context, tgClient: TgClient) {

    init {
        tgClient.updateNewChatFlow.onEach {
            add(it)
        }.launchIn(CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }

    private val chats = TgDataBase.getInstance(context).chats()

    fun add(chat: Chat) = chats.insert(chat)

    fun add(chat: TdApi.Chat) = chats.insert(
        Chat.fromTg(chat)
    )

    fun get(id: Long) = chats.getById(id)

    fun updateImage(file: TdApi.File) {
        chats.getList()?.find { it?.photoBig?.fileId == file.id }?.let {
            chats.updatePhoto(it.id, File.fromTg(file).localPath)
        }
    }

    fun addExampleChat() {
        add(Chat(id=1, title = "demo chat", photoBig = null))
    }
}