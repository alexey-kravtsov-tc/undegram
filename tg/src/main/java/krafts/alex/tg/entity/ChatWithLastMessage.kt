package krafts.alex.tg.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ChatWithLastMessage @JvmOverloads constructor(
    @ColumnInfo(name = "id") var chatId: Long,
    val text: String,
    var title: String,
    @Embedded val photoBig: File?
)