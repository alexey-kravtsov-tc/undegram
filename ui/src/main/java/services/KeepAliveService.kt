package services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class KeepAliveService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        private val LAUNCHER =
            ForegroundServiceLauncher(KeepAliveService::class.java)

        private val NOTIFICATION_ID = 10

        @JvmStatic
        fun start(context: Context) = LAUNCHER.startService(context)

        @JvmStatic
        fun stop(context: Context) = LAUNCHER.stopService(context)
    }

    override fun onCreate() {
        super.onCreate()
        val notification = Notification.Builder(this).notification
        startForeground(NOTIFICATION_ID, notification)
        //...

        LAUNCHER.onServiceCreated(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //...
        return START_NOT_STICKY
    }
}