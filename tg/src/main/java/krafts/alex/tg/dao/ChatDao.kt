package krafts.alex.tg.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import krafts.alex.tg.entity.Chat

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chat: Chat)

    @Query("SELECT * from chat where id = :id LIMIT 1")
    fun getById(id: Long): Chat

    @Query("SELECT * from chat")
    fun getList(): List<Chat>

    @Query("UPDATE chat SET localPath = :path, downloaded = :downloaded where id = :chatId")
    fun updatePhoto(chatId: Long, path: String, downloaded: Boolean = true)
}