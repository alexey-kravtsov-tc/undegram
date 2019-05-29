package krafts.alex.tg.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import krafts.alex.tg.entity.Message

@Dao
interface MessagesDao {

    @Insert
    fun insert(msg: Message)

    @Query("SELECT * from message where id = :id LIMIT 1")
    fun getById(id: Long): Message

    @Query("SELECT * from message ORDER BY id DESC LIMIT 25")
    fun getAll(): LiveData<List<Message>>

    @Query("SELECT * from message where deleted ORDER BY date DESC")
    fun getAllDeleted(): LiveData<List<Message>>

    @Query("UPDATE message SET deleted = :deleted where id = :id")
    fun markDeleted(id: Long, deleted: Boolean = true)

}

