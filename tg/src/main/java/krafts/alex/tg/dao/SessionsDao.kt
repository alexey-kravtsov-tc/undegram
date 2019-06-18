package krafts.alex.tg.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import krafts.alex.tg.entity.Session

@Dao
interface SessionsDao {

    @Insert
    fun add(session: Session)

    @Query("SELECT * from session where userId = :id ORDER BY start DESC LIMIT 1")
    fun getLastByUserId(id: Int): Session?

    @Query("SELECT * from session where userId = :id ORDER BY start ASC")
    fun getByUserId(id: Int): LiveData<List<Session>>

    @Query("SELECT sum(expires - start) from session where userId = :id and start > :start and expires < :end")
    fun getSumByUserIdForPeriod(id: Int, start: Int, end: Int) : Int

    @Query("UPDATE session SET expires = :end where id = :id")
    fun update(id: Int, end: Int)
}