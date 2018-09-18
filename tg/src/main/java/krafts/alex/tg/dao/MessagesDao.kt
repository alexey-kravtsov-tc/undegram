package krafts.alex.tg.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import krafts.alex.tg.entity.Message

@Dao
interface MessagesDao {

    @Insert
    fun insert(msg: Message)

    @Query("SELECT * from message where id = :id LIMIT 1")
    fun getById(id: Long) : Message

}