package com.alsrudsh0320.weather_notification.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alsrudsh0320.weather_notification.MainActivity
import com.alsrudsh0320.weather_notification.R

object NotificationHelper {

    private const val CHANNEL_ID = "weather_alarm_channel"
    private const val CHANNEL_NAME = "날씨 알림"

    fun showWeatherAlarmNotification(
        context: Context,
        regionCode: Long,
        title: String,
        message: String,
        @DrawableRes iconResId: Int
    ) {
        createChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("regionCode", regionCode)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            regionCode.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = BitmapFactory.decodeResource(
            context.resources,
            iconResId
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message.lines().firstOrNull() ?: message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (!canPostNotification(context)) return

        try {
            NotificationManagerCompat.from(context).notify(
                regionCode.toInt(),
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)
    }

    private fun canPostNotification(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return false
        }

        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}