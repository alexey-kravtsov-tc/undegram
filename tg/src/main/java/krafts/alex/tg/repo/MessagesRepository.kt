package krafts.alex.tg.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Chat
import krafts.alex.tg.entity.Message
import org.drinkless.td.libcore.telegram.TdApi

class MessagesRepository(context: Context) {

    private val msgs = TgDataBase.getInstance(context).messages()

    private val users = UsersRepository(context)

    private val chats = ChatRepository(context)

    fun getAll() = msgs.getAll()

    fun getAllRemoved(): LiveData<List<Message>> {
        return Transformations.map(msgs.getAllDeleted()) { msg ->
            msg.forEach {
                it.user = users.get(it.senderId)
                it.chat = if (it.senderId.toLong() == it.chatId) {
                    it.user?.let { usr -> Chat.fromUser(usr) } ?: chats.get(it.chatId)
                } else chats.get(it.chatId)
            }
            return@map msg
        }
    }

    fun add(msg: TdApi.Message) = msgs.insert(Message.fromTg(msg))

    fun get(id: Long) = msgs.getById(id)

    fun delete(id: Long) = msgs.markDeleted(id)
}