package arisumin.com.arisumin.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.ActivitySplashBinding
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.startActivityWithFinish
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.main.MainActivity
import arisumin.com.arisumin.view.start.ArisuInfoActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val resourceId: Int = R.layout.activity_splash

    private val pref by lazy { SplashPref(this) }
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(this)
                .asGif()
                .load(R.raw.splash)
                .listener(object: RequestListener<GifDrawable> {
                    override fun onResourceReady(resource: GifDrawable?, model: Any?,
                            target: Target<GifDrawable>?, dataSource: DataSource?,
                            isFirstResource: Boolean): Boolean {
                        resource?.setLoopCount(1)
                        handler.postDelayed({
                            nextActivity(pref.isSetting)
                        }, 3000)
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?,
                            target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                        nextActivity(pref.isSetting)
                        return false
                    }
                })
                .into(binding.root as ImageView)
    }

    private fun nextActivity(isSetting: Boolean) =
            if (isSetting) {
                startActivityWithFinish<MainActivity>()
            } else {
                startActivityWithFinish<ArisuInfoActivity>()
            }
}

private class SplashPref(context: Context) : PreferenceModel(context, PREF_NAME) {
    val isSetting by booleanPreference("isSetting")
}