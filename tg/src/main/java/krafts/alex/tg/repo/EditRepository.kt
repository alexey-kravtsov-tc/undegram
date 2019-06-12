package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Edit

class EditRepository(context: Context) {

    private val messageEdits = TgDataBase.getInstance(context).edits()

    fun add(edit: Edit) = messageEdits.add(edit)

    fun getForMessage(messageId: Long) = messageEdits.getByMessageId(messageId)
}