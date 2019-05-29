package krafts.alex.tg.entity

import org.drinkless.td.libcore.telegram.TdApi


data class File(
        val fileId: Int,
        val localPath: String,
        val downloaded: Boolean
) {
    companion object {

        fun fromTg(file: TdApi.File) = File(
                fileId = file.id,
                localPath = file.local.path,
                downloaded = file.local.isDownloadingCompleted
        )
    }
}