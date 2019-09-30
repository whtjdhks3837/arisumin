package arisumin.com.arisumin.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import arisumin.com.arisumin.R
import arisumin.com.arisumin.controller.AlarmController
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "sNotification"
        private const val CHANNEL_NAME = "sChannel"
        private const val DESCRIPTION = "water notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val calendar = Calendar.getInstance()
        context?.let {
            if (calendar.get(Calendar.HOUR_OF_DAY) in 0..6) {
                AlarmController(it.applicationContext).init()
                return
            }
            val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH).apply {
                    description = DESCRIPTION
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    setShowBadge(false)
                }.let { channel ->
                    notificationManager.createNotificationChannel(channel)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                    context,
                    AlarmController.REQ_CODE,
                    Intent(AlarmController.ACTION_NAME),
                    PendingIntent.FLAG_ONE_SHOT
            )

            notificationManager.notify(0, NotificationCompat.Builder(context, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.icon_main_01)
                setAutoCancel(true)
                setContentTitle("왕서운한수민")
                setContentIntent(pendingIntent)
            }.build())
        }
    }
}