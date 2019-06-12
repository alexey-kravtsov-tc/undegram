package krafts.alex.tg.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Message
import org.drinkless.td.libcore.telegram.TdApi

class MessagesRepository(context: Context) {

    private val msgs = TgDataBase.getInstance(context).messages()

    private val users = UsersRepository(context)

    private val chats = ChatRepository(context)

    fun getAll() = msgs.getAll()

    fun getAllRemoved(): LiveData<List<Message>> {
        return Transformations.map(msgs.getAllDeletedPerChat()) { msg ->
            msg.forEach {
                it.user = users.get(it.senderId)
                it.chat = chats.get(it)
            }
            return@map msg
        }
    }

    fun getRemovedForChat(chatId: Long): LiveData<List<Message>> {
        return Transformations.map(msgs.getAllDeletedForChat(chatId)) { msg ->
            msg.forEach {
                it.user = users.get(it.senderId)
                it.chat = chats.get(it)
            }
            return@map msg
        }
    }

    fun add(msg: TdApi.Message) = msgs.insert(Message.fromTg(msg))

    fun get(id: Long) = msgs.getById(id)

    fun delete(id: Long) = msgs.markDeleted(id)

    fun edit(id: Long, text: String) = msgs.edit(id, text)

}