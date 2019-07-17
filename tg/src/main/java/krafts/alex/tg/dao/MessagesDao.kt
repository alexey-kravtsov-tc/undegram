package krafts.alex.tg.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.MessageFromUserWithEdits

@Dao
interface MessagesDao {

    @Insert
    fun insert(msg: Message)

    @Query("SELECT * from message where id = :id LIMIT 1")
    fun getById(id: Long): Message?

    @Query("UPDATE message SET deleted = :deleted where id = :id")
    fun markDeleted(id: Long, deleted: Boolean = true)

    @Query("UPDATE message SET edited = :edited, date = :date, text = :text where id = :id")
    fun edit(id: Long, text: String, date: Int, edited: Boolean = true)

    //Chat list page

    @Query("SELECT Message.text, Chat.* from message left join chat on Chat.id == Message.chatId where deleted GROUP BY chatId ORDER BY date DESC")
    fun getAllDeletedPerChat(): DataSource.Factory<Int, ChatWithLastMessage>

    @Query("SELECT Message.text, Chat.* from message left join chat on Chat.id == Message.chatId where (deleted or edited) GROUP BY chatId ORDER BY date DESC")
    fun getAllDeletedAndEditedPerChat(): DataSource.Factory<Int, ChatWithLastMessage>

    //Chat page

    @Transaction
    @Query("SELECT distinct Message.id as messageId, Message.text, Message.senderId, Message.date, Message.edited, User.* from message left join user on User.id == Message.senderId where deleted AND chatId = :chatId ORDER BY date DESC")
    fun getAllDeletedForChat(chatId: Long): DataSource.Factory<Int, MessageFromUserWithEdits>

    @Transaction
    @Query("SELECT distinct Message.id as messageId, Message.text, Message.senderId, Message.date, Message.edited, User.* from message left join user on User.id == Message.senderId where (deleted or edited) AND chatId = :chatId ORDER BY date DESC")
    fun getAllDeletedAndEditedForChat(chatId: Long): DataSource.Factory<Int, MessageFromUserWithEdits>

    @Query("DELETE FROM message WHERE id = :id")
    fun delete(id: Long)
}