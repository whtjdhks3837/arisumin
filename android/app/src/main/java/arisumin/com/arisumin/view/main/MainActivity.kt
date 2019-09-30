package arisumin.com.arisumin.view.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arisumin.com.arisumin.bindColor
import arisumin.com.arisumin.databinding.ActivityMainBinding
import arisumin.com.arisumin.databinding.DialogDrinkBinding
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.htmlText
import arisumin.com.arisumin.service.TimerService
import arisumin.com.arisumin.startActivity
import arisumin.com.arisumin.view.stamp.StampAcitivity
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.base.BaseDialogFragment
import arisumin.com.arisumin.view.base.BaseQR
import arisumin.com.arisumin.view.map.MapActivity
import arisumin.com.arisumin.view.start.ArisuInfoActivity
import java.lang.IllegalStateException
import android.widget.Toast
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.DialogStampSuccessBinding
import arisumin.com.arisumin.util.ResourceUtil
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val resourceId = R.layout.activity_main
    private val statusBarColor by bindColor(R.color.colorPaleBlue)

    private val pref by lazy { MainPref(this, PREF_NAME) }
    private var oneDrinkAmount = 0.0f
    private val drinkDialog = DrinkDialog()
    private val stampSuccessDialog = StampSuccessDialog()

    private val cupLotties =
            listOf("cup/cup1.json", "cup/cup2.json", "cup/cup3.json", "cup/cup4.json", "cup/cup5.json")

    private var timerService: TimerService? = null
    private val onDayChangeCallback = {
        pref.intake = 0f
        runOnUiThread {
            updateCupLottie()
            initInfo()
        }
        Unit
    }

    private val testCouponString: String by lazy {
        "/img_2_pro/barcode/2% 아쿠아 340ml/2019.09.18./CU"
    }

    private val qrAcitivity by lazy {
        ArisuQR(this).apply {
            customize()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            service?.let {
                val binder = it as TimerService.Binder
                timerService = binder.service
                timerService?.addCallback(onDayChangeCallback)
                timerService?.timerStart()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            timerService?.removeCallback(onDayChangeCallback)
            timerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref.stampCount = 9
        window.statusBarColor = statusBarColor
        oneDrinkAmount = String.format("%.1f", pref.onceDrinkAmount).toFloat()

        initInfo()
        updateCupLottie()

        binding.drinkBtn.setOnClickListener { drinkDialog.show(supportFragmentManager, null) }
        binding.arisuWaterSpotBtn.setOnClickListener { startActivity<MapActivity>() }
        binding.arisuStampBtn.setOnClickListener { startActivity<StampAcitivity>() }
        binding.arisuInfoBtn.setOnClickListener { startActivity<ArisuInfoActivity>() }
        drinkDialog.drinkCallback = onDrinkCallback()
        drinkDialog.onQRStartCallback = onQRStartCallback()

        stampSuccessDialog.onShowStampCallback = onShowStampCallback()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TimerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(serviceConnection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                pref.intake += oneDrinkAmount
                countStamp()
                initInfo()
                updateCupLottie()
                showStampSuccessDialog()

                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateCupLottie() {
        when {
            pref.intake <= 0.0f -> {
                binding.waterLottie.setImageResource(R.drawable.main_water_cup)
                return
            }
            pref.oneDayRecommend <= pref.intake -> {
                binding.waterLottie.apply {
                    setAnimation(cupLotties.last())
                    playAnimation()
                }
            }
            else -> {
                val section = (pref.oneDayRecommend / cupLotties.size).toFloat()
                val level = getLevel(section)
                binding.waterLottie.apply {
                    setAnimation(cupLotties[level])
                    playAnimation()
                }
            }
        }
    }

    private fun getLevel(section: Float): Int {
        var intake = pref.intake
        for (i in cupLotties.indices) {
            if (section >= intake) {
                return i
            }
            intake -= section
        }
        throw IllegalStateException()
    }

    private fun initInfo() {
        binding.info.text = getString(R.string.main_my_info, pref.name)
        binding.figureIntake.text =
                htmlText(R.string.figure_intake, String.format("%.1f", pref.intake))
        binding.figureRecommend.text =
                htmlText(R.string.figure_recommend, String.format("%.1f", pref.oneDayRecommend))
        binding.figureGoal.text = htmlText(R.string.figure_goal, pref.goal)
        binding.figureStamp.text = htmlText(R.string.figure_stamp, pref.stampCount)
    }

    private fun countStamp() {
        pref.stampCount++

        if (pref.stampCount >= 10) {
            pref.stampCount = 0
            pref.couponList = pref.couponList.apply {
                add(pref.couponList.size.toString() + testCouponString)
            }
        }
    }

    private fun showStampSuccessDialog() {
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.add(stampSuccessDialog, null)
        fragmentManager.commitAllowingStateLoss()
    }

    private fun onDrinkCallback(): () -> Unit = {
        pref.intake += oneDrinkAmount
        initInfo()
        updateCupLottie()
    }

    private fun onQRStartCallback(): () -> Unit = {
        qrAcitivity.start()
    }

    private fun onShowStampCallback(): () -> Unit = {
        startActivity<StampAcitivity>()
    }
}

class DrinkDialog : BaseDialogFragment<DialogDrinkBinding>() {

    override val resourceId = R.layout.dialog_drink
    var drinkCallback: (() -> Unit)? = null
    var onQRStartCallback: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener { dismiss() }
        binding.qrBtn.setOnClickListener {
            dismiss()
            onQRStartCallback?.invoke()
        }
        binding.drinkBtn.setOnClickListener {
            drinkCallback?.invoke()
            dismiss()
        }
    }
}

class StampSuccessDialog : BaseDialogFragment<DialogStampSuccessBinding>() {
    override val resourceId: Int = R.layout.dialog_stamp_success
    var onShowStampCallback: (() -> Unit)? = null
    private val stampLottie = "stamp/stamp.json"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startStampLottie()
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.okButton.setOnClickListener { dismiss() }
        binding.startStampActivity.setOnClickListener {
            onShowStampCallback?.invoke()
        }
        binding.stampSuccessText.text = ResourceUtil(context!!).convertHtml(R.string.stamp_success_text)
    }


    private fun startStampLottie() {
        binding.stampLottie.apply {
            setAnimation(stampLottie)
            playAnimation()
        }
    }

}

class MainPref(context: Context, name: String) : PreferenceModel(context, name) {
    companion object {
        const val DRINK_PERIOD = 7
    }

    var name by stringPreference("name", null)
    var weight by intPreference("weight", 0)
    var intake by floatPreference("intake", 0.0f)
    var stampCount by intPreference("stampCount", 0)
    var couponList: MutableSet<String> by stringSetPreference("couponList", mutableSetOf())

    val oneDayRecommend = weight * 0.03
    val onceDrinkAmount = oneDayRecommend / DRINK_PERIOD
    val goal: Int
        get() {
            if (intake <= 0.0) {
                return 0
            }
            return ((intake / oneDayRecommend) * 100).toInt()
        }
}

class ArisuQR(override val parent: Any) : BaseQR() {
    override fun customize() {
        qr.setOrientationLocked(false)
    }
}

