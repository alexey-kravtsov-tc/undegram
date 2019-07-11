package krafts.alex.backupgram.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
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
import services.KeepAliveService

class BackApp : Application(), KodeinAware, LifecycleObserver {

    private val sessionRepository: SessionRepository by instance()

    override val kodein = lazy {
        import(androidXModule(this@BackApp))
        bind() from singleton { FirebaseAnalytics.getInstance(applicationContext) }
        bind() from singleton { SettingsRepository(applicationContext) }
        import(TgModule.resolve(applicationContext))
        import(ViewModelFactory.viewModelModule)
    }

    override fun onCreate() {
        client = TgClient(applicationContext)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        messages = MessagesRepository(applicationContext)
        users = UsersRepository(applicationContext)
        chats = ChatRepository(applicationContext)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            it.token.let { token -> client?.registerFirebaseNotifications(token) }
        }

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




    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun stopForegroundService() {
        val serviceIntent = Intent(applicationContext, KeepAliveService::class.java)

        if (KeepAliveService.isServiceRunning(this)) {
            stopService(serviceIntent)
        } else {
            Crashlytics.log("Service already stopped.")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun startForegroundService() {
        if (client.haveAuthorization) {
            val serviceClass = KeepAliveService::class.java
            val serviceIntent = Intent(applicationContext, serviceClass)

            if (!KeepAliveService.isServiceRunning(this)) {
                startService(serviceIntent)
            } else {
                Crashlytics.log("Service already running.")
            }
        }
    }

    companion object {
        lateinit var client: TgClient
        lateinit var messages: MessagesRepository
        lateinit var users: UsersRepository
        lateinit var chats: ChatRepository
    }
}

