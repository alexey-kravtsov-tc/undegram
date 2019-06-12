package krafts.alex.tg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Edit(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val messageId: Long,
    val date: Int,
    val text: String
) {
    
    companion object {
        
        fun fromMessage(message: Message) =
            Edit(
                id = 0,
                messageId = message.id,
                date = message.date,
                text = message.text
            )
    }
}