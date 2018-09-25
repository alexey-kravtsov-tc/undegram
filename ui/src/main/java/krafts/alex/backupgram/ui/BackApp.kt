package krafts.alex.backupgram.ui

import android.app.Application
import krafts.alex.tg.TgClient
import krafts.alex.tg.repo.MessagesRepository

class BackApp : Application() {

    override fun onCreate() {
        client = TgClient(applicationContext)
        messages = MessagesRepository(applicationContext)
        super.onCreate()
    }

    companion object {

        lateinit var messages: MessagesRepository
        lateinit var client: TgClient

    }
}

