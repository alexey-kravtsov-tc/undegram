package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.Edit
import java.util.concurrent.TimeUnit

class EditRepository(context: Context) {

    private val messageEdits = TgDataBase.getInstance(context).edits()

    fun add(edit: Edit) = messageEdits.add(edit)

    fun getForMessage(messageId: Long) = messageEdits.getByMessageId(messageId)

    fun addExampleEdits() {
        add(Edit(
            id = 0,
            messageId = 2,
            date = now() - 4 * 60,
            text = "and what has been edited with edit history"
        ))
        add(Edit(
            id = 0,
            messageId = 2,
            date = now() - 5 * 60,
            text = "and what has been edited with its timeline"
        ))

    }

    private fun now() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
}