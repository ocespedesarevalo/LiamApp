package com.example.liamapp.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "Medicamento"

        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora del medicamento")
            .setContentText("Es hora de tomar $medicationName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
