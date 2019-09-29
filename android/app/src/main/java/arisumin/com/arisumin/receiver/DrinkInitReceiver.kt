package arisumin.com.arisumin.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.log
import arisumin.com.arisumin.toast
import arisumin.com.arisumin.view.main.MainPref

class DrinkInitReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            MainPref(it, PREF_NAME).intake = 0f
            it toast ("drink init!!")
            log("drink init!!!!")
        }
    }
}