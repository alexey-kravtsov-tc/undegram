package krafts.alex.tg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity
data class Message(
    @PrimaryKey val id: Long,
    val senderId: Int,
    val chatId: Long,
    var text: String,
    val date: Int,
    var editDate: Int,
    var deleted: Boolean,
    var edited: Boolean
) {

    fun isPersonal() = senderId.toLong() == chatId

    companion object {

        fun fromTg(msg: TdApi.Message, text: String) = Message(
            id = msg.id,
            senderId = msg.senderUserId,
            chatId = msg.chatId,
            text = text,
            date = msg.date,
            editDate = msg.editDate,
            deleted = false,
            edited = false
        )
    }
}