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

    fun deletePermanently(message: Message) = msgs.delete(message)

    fun edit(id: Long, text: String) = msgs.edit(id, text, now())

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

    fun addExampleMessages() {
        msgs.insert(stub(2, "you can track what messages was removed from the chat", true))
        msgs.insert(stub(3, "and what has been edited", false, true))
        edits.addExampleEdits()
        msgs.insert(stub(4, "you can delete permanently those messages by swipe left", true))
        msgs.insert(stub(5, "you can see user online activity at the chart above", true))
        msgs.insert(stub(6, "User chat demo here", true))
    }

    private fun stub(
        id: Long,
        text: String,
        deleted: Boolean,
        edited: Boolean = false
    ) = Message(
        id = id,
        senderId = 1,
        chatId = 1,
        text = text,
        date = now() - id.toInt() * 60,
        editDate = 0,
        deleted = deleted,
        edited = edited,
        user = null,
        chat = null,
        edits = null
    )
}