package krafts.alex.tg.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import krafts.alex.tg.TgClient
import krafts.alex.tg.dao.ChatDao
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.File
import org.drinkless.td.libcore.telegram.TdApi

class ChatRepository(private val chats: ChatDao, tgClient: TgClient) {

    init {
        tgClient.updateNewChatFlow.onEach {
            add(it)
        }.launchIn(CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }


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