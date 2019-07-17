package krafts.alex.tg.entity

import androidx.room.Embedded

data class UserWithSessions(
    @Embedded val user: User,
    val sessionsTime: Long
)