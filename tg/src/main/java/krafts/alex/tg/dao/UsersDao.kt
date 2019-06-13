package krafts.alex.tg.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import krafts.alex.tg.entity.User

@Dao
interface UsersDao {

    @Insert(onConflict = REPLACE)
    fun insert(usr: User)

    @Query("SELECT * from user where id = :id LIMIT 1")
    fun getById(id: Int): User

    @Query("SELECT * from user")
    fun getList(): List<User>

    @Query("SELECT * from user")
    fun getRecent(): LiveData<List<User>>

    @Query("UPDATE user SET localPath = :path, downloaded = :downloaded where id = :userId")
    fun updatePhoto(userId: Int, path: String, downloaded: Boolean = true)

    @Query("UPDATE user SET notifyDelete = :delete, notifyOnline = :online where id = :userId")
    fun updateNotifications(
        userId: Int,
        delete: Boolean = false,
        online: Boolean = false
    )
}

