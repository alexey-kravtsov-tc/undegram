package krafts.alex.backupgram.ui

import android.app.Application
import krafts.alex.tg.TgClient
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.UsersRepository
import android.content.Context

class BackApp : Application() {

    override fun onCreate() {
        messages = MessagesRepository(applicationContext)
        users = UsersRepository(applicationContext)
        chats = ChatRepository(applicationContext)
        sessions = SessionRepository(applicationContext)
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        base?.let { DumbService.start(it) }
        super.attachBaseContext(base)
    }

    companion object {

        lateinit var client: TgClient
        lateinit var messages: MessagesRepository
        lateinit var users: UsersRepository
        lateinit var chats: ChatRepository
        lateinit var sessions: SessionRepository

    }
}

