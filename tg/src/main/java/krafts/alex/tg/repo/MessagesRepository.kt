package krafts.alex.tg.repo

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.room.Transaction
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.MessageFromUserWithEdits
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class MessagesRepository(context: Context) {

    private val msgs = TgDataBase.getInstance(context).messages()

    private val edits = EditRepository(context)

    fun getAllRemoved(hideEdit: Boolean): DataSource.Factory<Int, ChatWithLastMessage> =
        if (hideEdit) {
            msgs.getAllDeletedPerChat()
        } else {
            msgs.getAllDeletedAndEditedPerChat()
        }

    @Transaction
    fun getRemovedForChat(
        chatId: Long, hideEdit: Boolean
    ): DataSource.Factory<Int, MessageFromUserWithEdits> {
        return if (hideEdit) {
            msgs.getAllDeletedForChat(chatId)
        } else {
            msgs.getAllDeletedAndEditedForChat(chatId)
        }
    }

    fun add(msg: TdApi.Message, text: String) = msgs.insert(Message.fromTg(msg, text))

    fun get(id: Long) = msgs.getById(id)

    fun delete(id: Long) = msgs.markDeleted(id)

    fun deletePermanently(id: Long) = msgs.delete(id)

    fun edit(id: Long, text: String) = msgs.edit(id, text, now())

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

    fun addExampleMessages() {
        msgs.insert(stub(1, "you can track what messages are removed from Telegram chat", true))
        msgs.insert(stub(2, "and what has been edited", false, true))
        edits.addExampleEdits()
        msgs.insert(stub(3, "you can see user online activity at the chart above", true))
        msgs.insert(stub(4, "double tap on chart to zoom in", true))
        msgs.insert(stub(5, "swipe left to permanently delete message", true))
        msgs.insert(stub(6, "chat demo is here", true))
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
        edited = edited
    )
}