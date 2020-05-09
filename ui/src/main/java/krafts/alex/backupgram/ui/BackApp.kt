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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import krafts.alex.backupgram.ui.settings.SettingsRepository
import krafts.alex.tg.TgClient
import krafts.alex.tg.TgModule
import org.kodein.di.Kodein.Companion.lazy
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import services.KeepAliveService

class BackApp : Application(), KodeinAware, LifecycleObserver {

    private val addExamplesUseCase: AddExamplesUseCase by instance()

    override val kodein = lazy {
        import(androidXModule(this@BackApp))
        bind() from singleton { FirebaseAnalytics.getInstance(applicationContext) }
        bind() from singleton { SettingsRepository(applicationContext) }
        bind() from singleton { TgClient() }
        import(TgModule.resolve(applicationContext))
        bind() from singleton { AddExamplesUseCase(instance(), instance(), instance()) }
        import(ViewModelFactory.viewModelModule)
    }

    override fun onCreate() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
        //            it.token.let { token -> client?.registerFirebaseNotifications(token) }
        //        }

        if (!PreferenceManager
                .getDefaultSharedPreferences(applicationContext)
                .getBoolean("first_launch", false)
        ) {
            populateOnFirstStart()
        }

        super.onCreate()
    }

    private fun populateOnFirstStart() {

        //TODO: launch from a proper scope
        GlobalScope.launch {
            withContext(Dispatchers.IO onError {

            }) {
                addExamplesUseCase.addExamples()
            }
        }

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
        val serviceActive = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .getBoolean("service_active", false) //TODO: use repo

        val client : TgClient by instance()

        if (client.haveAuthorization && serviceActive) {
            val serviceIntent = Intent(applicationContext, KeepAliveService::class.java)

            if (!KeepAliveService.isServiceRunning(this)) {
                startService(serviceIntent)
            } else {
                Crashlytics.log("Service already running.")
            }
        }
    }
}

