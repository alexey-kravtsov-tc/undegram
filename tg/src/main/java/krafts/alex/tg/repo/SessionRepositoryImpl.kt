package krafts.alex.tg.repo

import androidx.paging.DataSource
import com.kizitonwose.time.Interval
import com.kizitonwose.time.Second
import com.kizitonwose.time.days
import com.kizitonwose.time.hours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import krafts.alex.tg.TgClient
import krafts.alex.tg.dao.SessionsDao
import krafts.alex.tg.entity.Session
import krafts.alex.tg.entity.UserWithSessions
import krafts.alex.tg.log
import krafts.alex.tg.repo.TgTime.nowInSeconds
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class SessionRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val tgClient: TgClient
) : SessionRepository {

    init {
        tgClient.userStatusFlow.onEach {
            "collect status $it".log()
            if (it.status is TdApi.UserStatusOnline) {
                updateSession(it)
            } else {
                endSession(it.userId)
            }
        }.launchIn(GlobalScope)
    }

    override fun updateSession(userStatus: TdApi.UpdateUserStatus) {
        (userStatus.status as? TdApi.UserStatusOnline)?.let { status ->
            sessionsDao.getLastByUserId(userStatus.userId)?.let { last ->
                if (last.expires > nowInSeconds()) {
                    sessionsDao.update(last.id, status.expires)
                } else {
                    addSession(userStatus.userId, status.expires)
                }
            } ?: addSession(userStatus.userId, status.expires)
        }
    }


    private fun addSession(userId: Int, expires: Int) {
        sessionsDao.add(Session.fromTg(userId, nowInSeconds(), expires))
    }

    override fun endSession(userId: Int) {
        sessionsDao.getLastByUserId(userId)?.let {
            if (it.expires > nowInSeconds()) {
                sessionsDao.update(it.id, nowInSeconds())
            }
        }
    }

    override fun getSessionsForUser(userId: Int) = sessionsDao.getByUserId(userId)

    override fun getUsersBySessionCount(start: Int, end: Int)
        : DataSource.Factory<Int, UserWithSessions> =
        sessionsDao.getUsersIdsByEditsCount(
            start = nowInSeconds() - start.hours.inSeconds.toInt(),
            end = nowInSeconds() - end.hours.inSeconds.toInt()
        )

    override suspend fun getYesterdayTotal(userId: Int): Int =
        sessionsDao.getSumByUserIdForPeriod(
            id = userId,
            start = nowInSeconds() - 2.days.inSeconds.toInt(),
            end = nowInSeconds() - 1.days.inSeconds.toInt()
        ) ?: 0

    override suspend fun getTodayTotal(userId: Int): Int =
        sessionsDao.getSumByUserIdForPeriod(
            id = userId,
            start = nowInSeconds() - 1.days.inSeconds.toInt(),
            end = nowInSeconds()
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
                start = nowInSeconds() - (minutesOffsetFromNow + 1) * 60,
                expires = nowInSeconds() - minutesOffsetFromNow * 60
            )
        )
    }

}

object TgTime {
    fun nowInSeconds() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
}

fun Interval<Second>.toInt() = Math.round(this.value).toInt()