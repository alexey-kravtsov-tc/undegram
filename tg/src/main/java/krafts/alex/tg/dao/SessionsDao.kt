package krafts.alex.tg.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import krafts.alex.tg.entity.Session
import krafts.alex.tg.entity.UserWithSessions

@Dao
interface SessionsDao {

    @Insert
    fun add(session: Session)

    @Query("SELECT * from session where userId = :id ORDER BY start DESC LIMIT 1")
    fun getLastByUserId(id: Int): Session?

    @Query(
        """
        select User.*, User.id as userId, abs(sum(expires - start)) as sessionsTime,
        :end as finish, :start as start
        from session left join user on User.id == Session.userId
        where start > :start and expires < :end
        group by userId order by sum(expires - start) desc
        """
    )
    fun getUsersIdsByEditsCount(start: Int, end: Int): DataSource.Factory<Int, UserWithSessions>

    @Query("SELECT * from session where userId = :id ORDER BY start ASC")
    fun getByUserId(id: Int): LiveData<List<Session>>

    @Query("SELECT sum(expires - start) from session where userId = :id and start > :start and expires < :end")
    suspend fun getSumByUserIdForPeriod(id: Int, start: Int, end: Int): Int?

    @Query("UPDATE session SET expires = :end where id = :id")
    fun update(id: Int, end: Int)
}