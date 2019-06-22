package krafts.alex.tg.repo

import android.content.Context
import krafts.alex.tg.R
import krafts.alex.tg.TgDataBase
import krafts.alex.tg.entity.File
import krafts.alex.tg.entity.User
import org.drinkless.td.libcore.telegram.TdApi

class UsersRepository(context: Context) {

    private val users = TgDataBase.getInstance(context).users()

    fun add(user: User) = users.insert(user)

    fun get(id: Int) = users.getById(id)

    fun getMostRecent() = users.getRecent()

    fun updateImage(file: TdApi.File) {
        users.getList().find { it.photoBig?.fileId == file.id }?.let {
            users.updatePhoto(it.id, File.fromTg(file).localPath)
        }
    }

    fun updateNotificationsSettings(
        userId: Int,
        delete: Boolean = false,
        online: Boolean = false
    ) = users.updateNotifications(userId, delete, online)

    private val ANDROID_RESOURCE = "android.resource://"
    private val FORWARD_SLASH = "/"
    val packageName = context.packageName

    fun addExampleUser() {
        users.insert(
            User(
                1, "Demo", "User", "", null, null,
                File(
                    fileId = 1,
                    localPath = "file:///android_asset/ic_demo.png", //TODO: make it rain
                    downloaded = true
                )
            )
        )
    }
}

