package arisumin.com.arisumin.view

import android.os.Bundle
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.ActivitySplashBinding
import arisumin.com.arisumin.view.base.BaseActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val resourceId: Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}