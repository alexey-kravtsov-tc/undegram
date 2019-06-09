package krafts.alex.tg.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity
data class Chat(
    @PrimaryKey val id: Long,
    val title: String,
    @Embedded val photoBig: File?
) {

    companion object {
        fun fromTg(chat: TdApi.Chat) = Chat(
            id = chat.id,
            title = chat.title,
            photoBig = chat.photo?.let { File.fromTg(it.big) }
        )
        fun fromUser(user: User) = Chat(
            id = user.id.toLong(),
            title = user.toString(),
            photoBig = user.photoBig
        )
    }
}