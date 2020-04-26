package services

import android.app.ActivityManager
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.HandlerThread
import android.os.IBinder
import com.crashlytics.android.Crashlytics
import krafts.alex.backupgram.ui.MainActivity

class KeepAliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val context = applicationContext
        createChannel(context)

        val notifyIntent = Intent(context, MainActivity::class.java)

        val title = "Undegram service"
        val message = CHANNEL_NAME

        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("message", message)
        notifyIntent.putExtra("notification", true)

        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent =
            PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        startForeground(
            716,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle().bigText(message)
                    )
                    .setContentText(message).build()
            } else {
                Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(
                        Notification.BigTextStyle().bigText(message)
                    )
                    .setContentText(message).build()
            }
        )

        Crashlytics.log("KeepAliveService started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        Crashlytics.log("KeepAliveService destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    private fun createChannel(context: Context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    enableVibration(true)
                    setShowBadge(true)
                    enableLights(true)
                    lightColor = Color.parseColor("#e8334a")
                    description = CHANNEL_NAME
                    lockscreenVisibility = Notification.VISIBILITY_SECRET
                }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.akrafts.undegram.notification.CHANNEL_ID"
        const val CHANNEL_NAME = "collecting info so you don't miss anything"

        fun isServiceRunning(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as
                ActivityManager

            // Loop through the running services
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (service.service.className == KeepAliveService::class.java.name) {
                    // If the service is running then return true
                    return true
                }
            }
            return false
        }
    }
}