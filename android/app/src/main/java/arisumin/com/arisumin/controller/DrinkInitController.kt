package arisumin.com.arisumin.controller

import android.app.AlarmManager
import android.app.AlarmManager.*
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.Calendar

class DrinkInitController {

    companion object {
        const val ACTION_NAME = "DrinkInitService"
        const val REQ_CODE = 0x02
    }

    fun init(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sender = PendingIntent.getBroadcast(context.applicationContext, REQ_CODE,
                Intent(ACTION_NAME), PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, SystemClock.elapsedRealtime() + 30000, sender)
//        alarmManager.setInexactRepeating(RTC_WAKEUP, getTriggerTime(), 10000L, sender)
    }

    private fun getTriggerTime() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis


}