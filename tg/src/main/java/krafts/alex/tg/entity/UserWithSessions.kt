package krafts.alex.tg.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithSessions(
    @Embedded val user: User,
    val sessionsTime: Long,
    @Relation(parentColumn = "id", entityColumn = "userId") val sessions: List<Session>
)