package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.TgDataBase

class MessagesRepository(context: Context) {

    private val msgs = TgDataBase.getInstance(context).messages()

    fun getAll() = msgs.getAll()

    fun getAllRemoved() = msgs.getAllDeleted()
}