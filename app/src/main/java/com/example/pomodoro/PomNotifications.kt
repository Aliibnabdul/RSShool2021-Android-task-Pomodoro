package com.example.pomodoro

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID = "Pomodoro_Channel_ID"
private const val CHANNEL_NAME = "Timer notification channel name"
private const val CHANNEL_DESCRIPTION = "Notification channel description"

class PomNotifications(private val context: Context) {
    private val notificationManagerCompat: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createChannel()
    }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Simple Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSmallIcon(R.drawable.ic_access_time)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManagerCompat.getNotificationChannel(CHANNEL_ID) == null) {
                val notificationChannel = NotificationChannelCompat.Builder(
                    CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_HIGH
                )
                    .setName(CHANNEL_NAME)
                    .setDescription(CHANNEL_DESCRIPTION)
                    .build()

                notificationManagerCompat.createNotificationChannel(notificationChannel)
            }
        }
    }

    fun refreshNotification(id: Int, newMessage: String, isSilent: Boolean) {
        notificationManagerCompat.notify(
            id,
            getNotification(newMessage, isSilent)
        )
    }

    fun getNotification(content: String, isSilent: Boolean): Notification {
        return notificationBuilder
            .setSilent(isSilent)
            .setContentText(content)
            .build()
    }
}