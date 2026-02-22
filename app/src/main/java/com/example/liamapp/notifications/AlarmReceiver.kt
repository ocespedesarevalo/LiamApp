package com.example.liamapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.liamapp.MainActivity
import com.example.liamapp.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra(EXTRA_MED_NAME) ?: "Medicamento"
        val medicationId = intent.getLongExtra(EXTRA_MED_ID, 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Sonido de alarma predeterminado
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) 
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        createNotificationChannel(context, notificationManager, alarmSound)

        // Crear Intent para abrir la app al tocar la notificación
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicationId.toInt(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medical_cross)
            .setContentTitle("¡Hora de la medicina!")
            .setContentText("Es momento de dar: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)) // Vibración intensa
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(Color.parseColor("#00796B"))
            .setFullScreenIntent(pendingIntent, true) // Hace que aparezca sobre otras cosas
            .build()

        notificationManager.notify(medicationId.toInt(), notification)
    }

    private fun createNotificationChannel(context: Context, manager: NotificationManager, soundUri: android.net.Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Medicación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para las alarmas de Liam App"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_MED_ID = "MEDICATION_ID"
        const val EXTRA_MED_NAME = "MEDICATION_NAME"
        const val CHANNEL_ID = "medication_reminders_v2" // Cambiamos ID para forzar recreación del canal con sonido
    }
}
