package krafts.alex.tg.repo

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import krafts.alex.tg.entity.Session
import krafts.alex.tg.entity.User
import krafts.alex.tg.entity.UserWithSessions
import org.drinkless.td.libcore.telegram.TdApi

interface SessionRepository {
    fun updateSession(userStatus: TdApi.UpdateUserStatus)
    fun endSession(userId: Int)
    fun getSessionsForUser(userId: Int): LiveData<List<Session>>
    fun getUsersBySessionCount(): DataSource.Factory<Int, UserWithSessions>
    suspend fun getYesterdayTotal(userId: Int): Int
    suspend fun getTodayTotal(userId: Int): Int
    fun addExampleSessions()
}