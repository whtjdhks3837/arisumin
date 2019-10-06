package arisumin.com.arisumin

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import arisumin.com.arisumin.controller.AlarmWorker
import java.util.concurrent.TimeUnit

class App : Application() {

    private val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(3, TimeUnit.HOURS).build()
    private val workManager by lazy { WorkManager.getInstance(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        workManager.enqueue(workRequest)
    }
}