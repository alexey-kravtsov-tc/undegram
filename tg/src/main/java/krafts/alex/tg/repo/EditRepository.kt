package krafts.alex.tg.repo

import krafts.alex.tg.dao.EditsDao
import krafts.alex.tg.entity.Edit
import krafts.alex.tg.repo.TgTime.nowInSeconds

class EditRepository(val messageEdits: EditsDao) {

    fun add(edit: Edit) = messageEdits.add(edit)

    fun getForMessage(messageId: Long) = messageEdits.getByMessageId(messageId)

    fun addExampleEdits() {
        add(Edit(
            id = 0,
            messageId = 2,
            date = nowInSeconds() - 4 * 60,
            text = "and what has been edited with edit history"
        ))
        add(Edit(
            id = 0,
            messageId = 2,
            date = nowInSeconds() - 5 * 60,
            text = "and what has been edited with its timeline"
        ))

    }

}