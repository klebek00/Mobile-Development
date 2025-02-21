package com.example.watertracker.api

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.watertracker.MainActivity
import com.example.watertracker.R

class ReminderReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("MyTag", "!!!!!!!!!!!")
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "watertracker:WakeLock"
        )
        wakeLock.acquire(3000) // Держим пробуждение 3 секунды
        showWaterReminderNotification(context)
        wakeLock.release()
    }

    private fun showWaterReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, "water_reminder_channel")
            .setSmallIcon(R.drawable.water_drop)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.water_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        notificationManager?.notify(0, builder.build())
    }
}