package krafts.alex.tg.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import krafts.alex.tg.entity.Message

@Dao
interface MessagesDao {

    @Insert
    fun insert(msg: Message)

    @Query("SELECT * from message where id = :id LIMIT 1")
    fun getById(id: Long): Message

    @Query("SELECT * from message ORDER BY id DESC LIMIT 25")
    fun getAll(): LiveData<List<Message>>

    @Query("SELECT * from message where deleted GROUP BY chatId ORDER BY date DESC")
    fun getAllDeletedPerChat(): LiveData<List<Message>>

    @Query("SELECT * from message where deleted AND chatId = :chatId ORDER BY date DESC")
    fun getAllDeletedForChat(chatId: Long): LiveData<List<Message>>

    @Query("UPDATE message SET deleted = :deleted where id = :id")
    fun markDeleted(id: Long, deleted: Boolean = true)

}

