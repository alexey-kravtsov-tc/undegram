package krafts.alex.tg.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
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
        @Ignore var user: User? = null
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
                user = null
        )

        private fun TdApi.MessageContent.getText(): String {
            if (this is TdApi.MessageText) {
                return this.text.text
            }
            return this.toString()
        }

    }
}