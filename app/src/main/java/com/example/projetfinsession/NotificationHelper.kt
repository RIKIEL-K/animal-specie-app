package com.example.projetfinsession

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper (private val context:Context){
    companion object{
        const val CHANNEL_ID_DEFAULT = "sensor_alerts_default"
        const val CHANNEL_ID_HIGH = "sensor_alerts_high"
        private const val CHANNEL_NAME_DEFAULT = "Alertes Température et Humidité "
        private const val CHANNEL_NAME_HIGH = "Alertes Urgentes Température et Humidité"
        private const val CHANNEL_DESCRIPTION_DEFAULT = "Notifications standard pour les alertes de capteurs"
        private const val CHANNEL_DESCRIPTION_HIGH = "Notifications prioritaires pour les alertes de capteurs"
    }
    // Initialisation de la classe, création des canaux de notification
    init {

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Création d'un canal de notification pour les alertes par defalut
            val defaultChannel = NotificationChannel(
                CHANNEL_ID_DEFAULT, CHANNEL_NAME_DEFAULT, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION_DEFAULT
            }
            // Création d'un canal de notification pour les alertes urgentes
            val highChannel = NotificationChannel(
                CHANNEL_ID_HIGH, CHANNEL_NAME_HIGH, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_HIGH
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(defaultChannel)
            notificationManager.createNotificationChannel(highChannel)
        }
    }
    fun sendWarningNotification(notificationId: Int, title: String, text: String) {
        // Vérifie si les notifications sont activées pour l'application
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            // Crée la notification avec le canal correspondant
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_warning) // Icône de la notification
                .setContentTitle(title) // Titre de la notification
                .setContentText(text) // Texte de la notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priorité par défaut
                .build()

            // Envoie la notification
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    // Méthode pour envoyer une notification urgente
    fun sendUrgentNotification(notificationId: Int, title: String, text: String) {
        // Vérifie si les notifications sont activées pour l'application
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            // Crée la notification avec le canal correspondant
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_HIGH)
                .setSmallIcon(R.drawable.ic_urgent) // Icône de la notification
                .setContentTitle(title) // Titre de la notification
                .setContentText(text) // Texte de la notification
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorité élevée
                .build()

            // Envoie la notification
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}

