package services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import krafts.alex.backupgram.ui.BackApp

class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        token?.let { BackApp.client?.registerFirebaseNotifications(it) }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
    }
}