package krafts.alex.tg.repo

import android.content.Context
import com.kizitonwose.time.days
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Session
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.TimeUnit

class SessionRepository(context: Context) {
    private val sessions = TgDataBase.getInstance(context).sessions()

    fun updateSession(userStatus: TdApi.UpdateUserStatus) {
        (userStatus.status as? TdApi.UserStatusOnline)?.let { status ->
            sessions.getLastByUserId(userStatus.userId)?.let { last ->
                if (last.expires > now()) {
                    sessions.update(last.id, status.expires)
                } else {
                    addSession(userStatus.userId, status.expires)
                }
            } ?: addSession(userStatus.userId, status.expires)
        }
    }

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()

    private fun addSession(userId: Int, expires: Int) {
        sessions.add(Session.fromTg(userId, now(), expires))
    }

    fun endSession(userId: Int) {
        sessions.getLastByUserId(userId)?.let {
            if (it.expires > now()) {
                sessions.update(it.id, now())
            }
        }
    }

    fun getSessionsForUser(userId: Int) = sessions.getByUserId(userId)

    fun getYesterdayTotal(userId: Int): Int =
        sessions.getSumByUserIdForPeriod(
            id = userId,
            start = now() - 2.days.inSeconds.longValue.toInt(),
            end = now() - 1.days.inSeconds.longValue.toInt()
        )

    fun getTodayTotal(userId: Int): Int =
        sessions.getSumByUserIdForPeriod(
            id = userId,
            start = now() - 1.days.inSeconds.longValue.toInt(),
            end = now()
        )

}