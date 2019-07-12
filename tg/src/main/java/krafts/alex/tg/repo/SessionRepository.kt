package krafts.alex.tg.repo

import androidx.lifecycle.LiveData
import krafts.alex.tg.entity.Session
import krafts.alex.tg.entity.User
import org.drinkless.td.libcore.telegram.TdApi

interface SessionRepository {
    fun updateSession(userStatus: TdApi.UpdateUserStatus)
    fun endSession(userId: Int)
    fun getSessionsForUser(userId: Int): LiveData<List<Session>>
    suspend fun getUsersBySessionCount(): List<User>
    fun getYesterdayTotal(userId: Int): Int
    fun getTodayTotal(userId: Int): Int
    fun addExampleSessions()
}