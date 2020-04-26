package services

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.crashlytics.android.Crashlytics
import krafts.alex.backupgram.ui.R

class KeepAliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action = intent.action
        if (ACTION_DISMISS == action) {
            stopForeground(true)
            Crashlytics.log("KeepAliveService dismissed")
        } else {
            createChannel()

            startForeground(
                NOTIFICATION_ID,
                buildNotification()
            )
            Crashlytics.log("KeepAliveService started")
        }

        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val title = "Working background"
        val message = CHANNEL_NAME

        val notifyIntent = Intent(this, this::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent =
            PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, CHANNEL_ID)
        } else {
            Notification.Builder(applicationContext)
        }.setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_edits_show)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_edits_show
                )
            )
            .addAction(createStopAction())
            .setStyle(
                Notification.BigTextStyle().bigText(message)
            )
            .setContentText(message)
            .build()
    }

    private fun createStopAction(): Notification.Action? {
        val stopIntent = Intent(this, KeepAliveService::class.java)
        stopIntent.action = ACTION_DISMISS

        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        return Notification.Action.Builder(
            R.drawable.ic_edits_hidden,
            "Stop",
            stopPendingIntent
        ).build()
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

    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                NotificationManagerCompat.from(applicationContext)

            val importance = NotificationManager.IMPORTANCE_LOW
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

    private fun handleActionDismiss() {
        NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_ID)
    }

    companion object {
        const val NOTIFICATION_ID = 716
        const val ACTION_DISMISS = "com.akrafts.undegram.notification.DISMISS"
        const val CHANNEL_ID = "com.akrafts.undegram.notification.CHANNEL_ID"
        const val CHANNEL_NAME = "Collecting info so you don't miss anything"

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