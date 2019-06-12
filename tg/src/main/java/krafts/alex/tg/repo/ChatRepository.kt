package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.File
import krafts.alex.tg.entity.Message
import org.drinkless.td.libcore.telegram.TdApi

class ChatRepository(context: Context) {

    private val chats = TgDataBase.getInstance(context).chats()

    fun add(chat: Chat) = chats.insert(chat)

    fun add(chat: TdApi.Chat) = chats.insert(
        Chat.fromTg(chat)
    )

    fun get(id: Long) = chats.getById(id)

    fun get(message: Message) = if (message.senderId.toLong() == message.chatId) {
        message.user?.let { usr -> Chat.fromUser(usr) } ?: get(message.chatId)
    } else get(message.chatId)

    fun getAll() = chats.getList()

    fun updateImage(file: TdApi.File) {
        chats.getList()?.find { it?.photoBig?.fileId == file.id }?.let {
            chats.updatePhoto(it.id, File.fromTg(file).localPath)
        }
    }
}