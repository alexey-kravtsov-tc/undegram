package krafts.alex.tg.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithSessions(
    @Embedded val user: User,
    val sessionsTime: Long,
    val start: Int,
    val finish: Int,
    @Relation(parentColumn = "id", entityColumn = "userId") val sessions: List<Session>
)