package krafts.alex.backupgram.ui

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.TgClient
import krafts.alex.tg.TgModule
import krafts.alex.tg.repo.ChatRepository
import krafts.alex.tg.repo.MessagesRepository
import krafts.alex.tg.repo.SessionRepository
import krafts.alex.tg.repo.UsersRepository
import org.kodein.di.Kodein.Companion.lazy
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import services.DumbService

class BackApp : Application(), KodeinAware {

    private val sessionRepository : SessionRepository by instance()

    override val kodein = lazy {
        import(androidXModule(this@BackApp))
        bind() from singleton { FirebaseAnalytics.getInstance(applicationContext) }
        bind() from singleton { SettingsRepository(applicationContext) }
        import(TgModule.resolve(applicationContext))
        import(ViewModelFactory.viewModelModule)
    }

    override fun onCreate() {
        messages = MessagesRepository(applicationContext)
        users = UsersRepository(applicationContext)
        chats = ChatRepository(applicationContext)
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
        sessionRepository.addExampleSessions()
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
    }
}

