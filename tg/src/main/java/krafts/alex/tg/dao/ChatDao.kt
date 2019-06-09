package krafts.alex.tg.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import krafts.alex.tg.entity.Chat

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chat: Chat)

    @Query("SELECT * from chat where id = :id LIMIT 1")
    fun getById(id: Long): Chat?

    @Query("SELECT * from chat")
    fun getList(): List<Chat?>?

    @Query("UPDATE chat SET localPath = :path, downloaded = :downloaded where id = :chatId")
    fun updatePhoto(chatId: Long, path: String, downloaded: Boolean = true)
}