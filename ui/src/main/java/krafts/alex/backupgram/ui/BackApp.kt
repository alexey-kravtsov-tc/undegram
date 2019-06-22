package krafts.alex.backupgram.ui

import android.app.Application
import krafts.alex.tg.TgClient
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.UsersRepository
import android.content.Context
import androidx.preference.PreferenceManager
import services.DumbService

class BackApp : Application() {

    override fun onCreate() {
        messages = MessagesRepository(applicationContext)
        users = UsersRepository(applicationContext)
        chats = ChatRepository(applicationContext)
        sessions = SessionRepository(applicationContext)
        loginClient = TgClient(applicationContext)


        if (!PreferenceManager
                .getDefaultSharedPreferences(applicationContext)
                .getBoolean("first_launch", false)
        ) {
            populateOnFirstStart()
        }

        super.onCreate()
    }

    private fun populateOnFirstStart() {

        users.addExampleUser()
        messages.addExampleMessages()
        sessions.addExampleSessions()
        chats.addExampleChat()

        PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .edit()
            .putBoolean("first_launch", true)
            .apply()
    }

    companion object {

        fun startService(context: Context) {
            loginClient = null
            DumbService.start(context)
        }

        var loginClient: TgClient? = null
        lateinit var messages: MessagesRepository
        lateinit var users: UsersRepository
        lateinit var chats: ChatRepository
        lateinit var sessions: SessionRepository
    }
}

