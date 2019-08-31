package arisumin.com.arisumin.view

import android.os.Bundle
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.ActivityMainBinding
import arisumin.com.arisumin.view.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val resourceId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
