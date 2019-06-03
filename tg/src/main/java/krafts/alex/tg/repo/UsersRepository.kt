package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.File
import krafts.alex.tg.entity.User
import org.drinkless.td.libcore.telegram.TdApi

class UsersRepository(context: Context) {

    private val users = TgDataBase.getInstance(context).users()

    fun add(user: User) = users.insert(user)

    fun add(user: TdApi.User) = users.insert(
        User.fromTg(user)
    )

    fun get(id: Int) = users.getById(id)

    fun getAll() = users.getList()

    fun updateImage(file: TdApi.File) {
        users.getList().find { it.photoBig?.fileId == file.id }?.let {
            users.updatePhoto(it.id, File.fromTg(file).localPath)
        }
    }
}

