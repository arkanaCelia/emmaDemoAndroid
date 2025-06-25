package com.example.emma_test_android.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.emma.android.enums.EMMAPushType
import io.emma.android.push.EMMAPushNotificationsManager

/**
 * Clase para integrar notificaciones push fuera de EMMA
 */

class FirebaseServices: FirebaseMessagingService() {

    private fun isPushFromEMMA(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["eMMa"] == "1"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (isPushFromEMMA(remoteMessage)) {
            EMMAPushNotificationsManager.handleNotification(applicationContext, remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        EMMAPushNotificationsManager.refreshToken(applicationContext, token, EMMAPushType.FCM)
    }
}