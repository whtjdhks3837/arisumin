package arisumin.com.arisumin.controller

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.SystemClock
import java.util.Calendar

class AlarmController(private val context: Context) {

    companion object {
        const val ACTION_NAME = "AlarmService"
        const val REQ_CODE = 0x01
    }

    fun init() {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val sender = PendingIntent.getBroadcast(context.applicationContext, REQ_CODE, Intent(
                ACTION_NAME), FLAG_CANCEL_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, sender)
//        alarmManager.setInexactRepeating(RTC_WAKEUP, getTriggerTime(),
//                AlarmManager.INTERVAL_HOUR * 2 + AlarmManager.INTERVAL_FIFTEEN_MINUTES, sender)
    }

    private fun getTriggerTime() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 6)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}