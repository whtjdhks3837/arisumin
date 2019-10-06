package arisumin.com.arisumin.service

import android.app.Service
import android.content.Context
import android.content.Intent
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import java.text.DateFormat
import java.util.Date

class TimerService : Service() {

    companion object {
        const val PERIOD_MILLIS = 10000L
    }

    private val pref by lazy { TimePref(this) }
    private val dateFormat = DateFormat.getDateInstance()
    private lateinit var lastDate: Date

    private val binder = Binder()
    private val callbacks = mutableListOf<() -> Unit>()

    private var isStop = false

    private val runnable = Runnable {
        while (!isStop) {
            if (dateFormat.format(lastDate) != dateFormat.format(Date(System.currentTimeMillis()))) {
                pref.intake = 0f
                pref.date = System.currentTimeMillis()
                lastDate = Date(pref.date)
                callbacks.forEach { it.invoke() }
            }
            try {
                Thread.sleep(PERIOD_MILLIS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    private val thread = Thread(runnable)

    override fun onBind(p0: Intent?) = binder

    override fun onCreate() {
        super.onCreate()
        lastDate = Date(pref.date)
    }

    override fun onDestroy() {
        super.onDestroy()
        isStop = true
        thread.interrupt()
    }

    fun addCallback(callback: () -> Unit) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: () -> Unit) {
        callbacks.find { it === callback }?.let {
            callbacks.remove(callback)
        }
    }

    fun timerStart() = thread.start()

    inner class Binder : android.os.Binder() {
        val service = this@TimerService
    }
}

class TimePref(context: Context) : PreferenceModel(context, PREF_NAME) {

    var date by longPreference("date", System.currentTimeMillis())
    var intake by floatPreference("intake", 0.0f)
}