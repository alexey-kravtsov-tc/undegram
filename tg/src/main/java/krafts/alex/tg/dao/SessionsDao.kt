package krafts.alex.tg.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import krafts.alex.tg.entity.Message
import krafts.alex.tg.entity.Session

@Dao
interface SessionsDao {

    @Insert
    fun add(session: Session)

    @Query("SELECT * from session where userId = :id ORDER BY start DESC LIMIT 1")
    fun getLastByUserId(id: Int): Session?

    @Query("SELECT * from session where userId = :id ORDER BY start DESC")
    fun getByUserId(id: Long): LiveData<List<Session>>

    @Query("UPDATE session SET expires = :end where id = :id")
    fun update(id: Int, end: Int)
}