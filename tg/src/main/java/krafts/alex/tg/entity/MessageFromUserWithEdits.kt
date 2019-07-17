package krafts.alex.tg.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MessageFromUserWithEdits(
    val messageId: Long,
    var text: String,
    val senderId: Int,
    val date: Int,
    var edited: Boolean,
    @Embedded val user: User,
    @Relation(parentColumn = "messageId", entityColumn = "messageId") val edits: List<Edit>
)