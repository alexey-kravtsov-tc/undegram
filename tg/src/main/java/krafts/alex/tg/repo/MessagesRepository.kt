package krafts.alex.tg.repo

import android.util.Log
import androidx.paging.DataSource
import androidx.room.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import krafts.alex.tg.TgClient
import krafts.alex.tg.dao.MessagesDao
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.entity.Edit
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.MessageFromUserWithEdits
import krafts.alex.tg.log
import krafts.alex.tg.repo.TgTime.nowInSeconds
import org.drinkless.td.libcore.telegram.TdApi

@ExperimentalCoroutinesApi
class MessagesRepository(
    private val messagesDao: MessagesDao,
    private val editRepository: EditRepository, //TODO: move from here
    tgClient: TgClient
) {

    fun TdApi.MessageContent.text(): String {
        val name = this.javaClass.simpleName
        return when (this) {
            is TdApi.MessageText -> this.text.text
            is TdApi.MessagePhoto ->
                "[$name] ${this.caption?.text}"
            is TdApi.MessageVideo ->
                "[$name] ${this.caption?.text}"
            is TdApi.MessageAnimation ->
                "[$name] ${this.caption?.text}"
            else -> "[$name]"
        }
    }

    init {
        GlobalScope.launch {

            launch {
                "newMessageFlow".log()
                tgClient.newMessageFlow.collect {
                    "new message ${it.message.content.text()}".log()
                    add(it.message, it.message.content.text())

                }

            }

            launch {
                tgClient.updateMessageFlow.collect {
                    get(it.messageId)?.let { origin ->

                        editRepository.add(Edit.fromMessage(origin))
                        edit(it.messageId, it.newContent.text())

                        val before = origin.text
                        val after = it.newContent.text()

                        Log.e("~~~~~~~edited", "from $before to $after")
                    }
                }

            }

            launch {
                tgClient.deleteMessageFlow.collect {
                    if (it.isPermanent) {
                        for (id in it.messageIds) {
                            val message = get(id)
                            Log.e("======removed", message?.text)
                            delete(id)
                        }

                        /*

                          private val notificationCompat = NotificationCompat.Builder(context, "tg")

    private val notificationManager = NotificationManagerCompat.from(context)

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

                        val user = users.get(message?.senderId ?: 0)
                        if (message?.isPersonal() == true && (user?.notifyDelete == true
                                || preferences.getBoolean("notify_private", false))
                        ) {
                            val not = notificationCompat
                                .setSmallIcon(R.drawable.ic_delete)
                                .setContentTitle("${user?.firstName} deleted message")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentText(message.text).build()

                            notificationManager.notify(123, not)
                        }
                        */
                    }
                }
            }
        }
    }

    fun getAllRemoved(hideEdit: Boolean): DataSource.Factory<Int, ChatWithLastMessage> =
        if (hideEdit) {
            messagesDao.getAllDeletedPerChat()
        } else {
            messagesDao.getAllDeletedAndEditedPerChat()
        }

    @Transaction
    fun getRemovedForChat(
        chatId: Long, hideEdit: Boolean
    ): DataSource.Factory<Int, MessageFromUserWithEdits> {
        return if (hideEdit) {
            messagesDao.getAllDeletedForChat(chatId)
        } else {
            messagesDao.getAllDeletedAndEditedForChat(chatId)
        }
    }

    fun add(msg: TdApi.Message, text: String) = messagesDao.insert(Message.fromTg(msg, text))

    fun get(id: Long) = messagesDao.getById(id)

    fun delete(id: Long) = messagesDao.markDeleted(id)

    fun deletePermanently(id: Long) = messagesDao.delete(id)

    fun edit(id: Long, text: String) = messagesDao.edit(id, text, nowInSeconds())

    fun addExampleMessages() {
        messagesDao.insert(
            stub(
                1,
                "you can track what messages are removed from Telegram chat",
                true
            )
        )
        messagesDao.insert(stub(2, "and what has been edited", false, true))
        editRepository.addExampleEdits()
        messagesDao.insert(stub(3, "you can see user online activity at the chart above", true))
        messagesDao.insert(stub(4, "double tap on chart to zoom in", true))
        messagesDao.insert(stub(5, "swipe left to permanently delete message", true))
        messagesDao.insert(stub(6, "chat demo is here", true))
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
        date = nowInSeconds() - id.toInt() * 60,
        editDate = 0,
        deleted = deleted,
        edited = edited
    )
}