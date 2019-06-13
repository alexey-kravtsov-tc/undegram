package krafts.alex.tg.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Message
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class MessagesRepository(context: Context) {

    private val msgs = TgDataBase.getInstance(context).messages()

    private val users = UsersRepository(context)

    private val chats = ChatRepository(context)

    private val edits = EditRepository(context)

    fun getAll() = msgs.getAll()

    fun getAllRemoved(hideEdit: Boolean): LiveData<List<Message>> {
        val messages = if (hideEdit) {
            msgs.getAllDeletedPerChat()
        } else {
            msgs.getAllDeletedAndEditedPerChat()
        }
        return Transformations.map(messages) { msg ->
            msg.forEach {
                it.user = users.get(it.senderId)
                it.chat = chats.get(it)
            }
            return@map msg
        }
    }

    fun getRemovedForChat(chatId: Long, hideEdit: Boolean): LiveData<List<Message>> {
        val messages = if (hideEdit) {
            msgs.getAllDeletedForChat(chatId)
        } else {
            msgs.getAllDeletedAndEditedForChat(chatId)
        }
        return Transformations.map(messages) { msg ->
            msg.forEach {
                it.user = users.get(it.senderId)
                it.chat = chats.get(it)
                it.edits = edits.getForMessage(it.id)
            }
            return@map msg
        }
    }

    fun add(msg: TdApi.Message) = msgs.insert(Message.fromTg(msg))

    fun get(id: Long) = msgs.getById(id)

    fun delete(id: Long) = msgs.markDeleted(id)

    fun edit(id: Long, text: String) = msgs.edit(id, text, now())

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

}