package krafts.alex.tg.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import krafts.alex.tg.entity.User

@Dao
interface UsersDao {

    @Insert(onConflict = IGNORE)
    fun insert(usr: User)

    @Query("SELECT * from user where id = :id LIMIT 1")
    fun getById(id: Int): User

    @Query("SELECT * from user")
    fun getList(): List<User>

    @Query("UPDATE user SET localPath = :path, downloaded = :downloaded where id = :userId")
    fun updatePhoto(userId: Int, path: String, downloaded: Boolean = true)

    @Query("UPDATE user SET notifyDelete = :delete, notifyOnline = :online where id = :userId")
    fun updateNotifications(
        userId: Int,
        delete: Boolean = false,
        online: Boolean = false
    )
}

