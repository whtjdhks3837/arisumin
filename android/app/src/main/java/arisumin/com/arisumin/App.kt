package arisumin.com.arisumin

import android.app.Application
import arisumin.com.arisumin.controller.DrinkInitController

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DrinkInitController().init(applicationContext)
    }
}