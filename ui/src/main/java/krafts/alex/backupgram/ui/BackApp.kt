package krafts.alex.backupgram.ui

import android.app.Application
import krafts.alex.tg.TgClient
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.UsersRepository

class BackApp : Application() {

    override fun onCreate() {
        client = TgClient(applicationContext)
        messages = MessagesRepository(applicationContext)
        users = UsersRepository(applicationContext)
        super.onCreate()
    }

    companion object {

        lateinit var messages: MessagesRepository
        lateinit var users: UsersRepository
        lateinit var client: TgClient

    }
}

