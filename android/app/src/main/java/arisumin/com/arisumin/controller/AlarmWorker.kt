package arisumin.com.arisumin.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import arisumin.com.arisumin.R
import arisumin.com.arisumin.log
import arisumin.com.arisumin.receiver.AlarmReceiver
import java.util.Calendar

class AlarmWorker(
        private val context: Context,
        private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    companion object {
        private const val CHANNEL_ID = "sNotification"
        private const val CHANNEL_NAME = "sChannel"
        private const val DESCRIPTION = "water notification"
    }

    override fun doWork(): Result {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) in 0..6) {
            return Result.success()
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

        notificationManager.notify(0, NotificationCompat.Builder(context,
                CHANNEL_ID).apply {
            color = context.getColor(R.color.colorDeepBlue)
            setSmallIcon(R.drawable.icon_main_01)
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setAutoCancel(true)
            setContentTitle("수수한")
            setContentText("지금은 물 마실 시간!")
            setContentIntent(pendingIntent)
        }.build())
        return Result.success()
    }
}