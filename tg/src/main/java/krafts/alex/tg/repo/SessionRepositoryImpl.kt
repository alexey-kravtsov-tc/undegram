package krafts.alex.tg.repo

import androidx.paging.DataSource
import com.kizitonwose.time.Interval
import com.kizitonwose.time.Second
import com.kizitonwose.time.days
import com.kizitonwose.time.hours
import krafts.alex.tg.dao.SessionsDao
import krafts.alex.tg.entity.Session
import krafts.alex.tg.entity.UserWithSessions
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class SessionRepositoryImpl(
    private val sessionsDao: SessionsDao
) : SessionRepository {

    override fun updateSession(userStatus: TdApi.UpdateUserStatus) {
        (userStatus.status as? TdApi.UserStatusOnline)?.let { status ->
            sessionsDao.getLastByUserId(userStatus.userId)?.let { last ->
                if (last.expires > now()) {
                    sessionsDao.update(last.id, status.expires)
                } else {
                    addSession(userStatus.userId, status.expires)
                }
            } ?: addSession(userStatus.userId, status.expires)
        }
    }

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

    private fun addSession(userId: Int, expires: Int) {
        sessionsDao.add(Session.fromTg(userId, now(), expires))
    }

    override fun endSession(userId: Int) {
        sessionsDao.getLastByUserId(userId)?.let {
            if (it.expires > now()) {
                sessionsDao.update(it.id, now())
            }
        }
    }

    override fun getSessionsForUser(userId: Int) = sessionsDao.getByUserId(userId)

    override fun getUsersBySessionCount(start: Int, end: Int)
        : DataSource.Factory<Int, UserWithSessions> =
        sessionsDao.getUsersIdsByEditsCount(
            start = now() - start.hours.inSeconds.toInt(),
            end = now() - end.hours.inSeconds.toInt()
        )

    override suspend fun getYesterdayTotal(userId: Int): Int =
        sessionsDao.getSumByUserIdForPeriod(
            id = userId,
            start = now() - 2.days.inSeconds.toInt(),
            end = now() - 1.days.inSeconds.toInt()
        ) ?: 0

    override suspend fun getTodayTotal(userId: Int): Int =
        sessionsDao.getSumByUserIdForPeriod(
            id = userId,
            start = now() - 1.days.inSeconds.toInt(),
            end = now()
        ) ?: 0

    override fun addExampleSessions() {
        addExample(2000)
        addExample(2200)
        addExample(2202)
        addExample(2300)
        addExample(2400)


        addExample(45)
        addExample(55)
        addExample(58)
        addExample(35)
        addExample(85)
        addExample(65)
    }

    private fun addExample(minutesOffsetFromNow: Int) {
        sessionsDao.add(
            Session(
                id = 0,
                userId = 1,
                start = now() - (minutesOffsetFromNow + 1) * 60,
                expires = now() - minutesOffsetFromNow * 60
            )
        )
    }
}

fun Interval<Second>.toInt() = Math.round(this.value).toInt()