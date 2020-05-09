package krafts.alex.tg.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import krafts.alex.tg.TgClient
import krafts.alex.tg.dao.UsersDao
import krafts.alex.tg.entity.File
import krafts.alex.tg.entity.User
import org.drinkless.td.libcore.telegram.TdApi

class UsersRepository(private val users: UsersDao, tgClient: TgClient ) {

    init {
        CoroutineScope(SupervisorJob()).launch {
            tgClient.userFlow.collect {
                val user = User.fromTg(it.user)
                add(user)
                if (user.photoBig?.downloaded == false) {
                    val photo = tgClient.downloadFile(user.photoBig.fileId)
                    if (photo.local.isDownloadingCompleted) {
                        updateImage(photo)
                    }
                }
            }
        }
    }

    fun add(user: User) = users.insert(user)

    fun get(id: Int) = users.getById(id)

    suspend fun getAsync(id: Int) = users.getByIdAsync(id)

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

    fun addExampleUser() {
        users.insert(
            User(
                1, "Demo", "User", "", "", null, null,
                File(
                    fileId = 1,
                    localPath = "file:///android_asset/ic_demo.png", //TODO: make it rain
                    downloaded = true
                )
            )
        )
    }
}

