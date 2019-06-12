package krafts.alex.tg.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity
data class Message @JvmOverloads constructor(
    @PrimaryKey val id: Long,
    val senderId: Int,
    val chatId: Long,
    var text: String,
    val date: Int,
    var editDate: Int,
    var deleted: Boolean,
    var edited: Boolean,
    @Ignore var user: User? = null,
    @Ignore var chat: Chat? = null
) {

    companion object {

        fun fromTg(msg: TdApi.Message) = Message(
            id = msg.id,
            senderId = msg.senderUserId,
            chatId = msg.chatId,
            text = msg.content.getText(),
            date = msg.date,
            editDate = msg.editDate,
            deleted = false,
            edited = false,
            user = null,
            chat = null
        )

        private fun TdApi.MessageContent.getText(): String {
            if (this is TdApi.MessageText) {
                return this.text.text
            }
            return this.toString()
        }
    }
}