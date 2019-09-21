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
import arisumin.com.arisumin.R
import arisumin.com.arisumin.bindColor
import arisumin.com.arisumin.databinding.ActivityMainBinding
import arisumin.com.arisumin.databinding.DialogDrinkBinding
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.htmlText
import arisumin.com.arisumin.log
import arisumin.com.arisumin.service.TimerService
import arisumin.com.arisumin.startActivity
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.base.BaseDialogFragment
import arisumin.com.arisumin.view.map.MapActivity
import java.lang.IllegalStateException

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val resourceId = R.layout.activity_main
    private val statusBarColor by bindColor(R.color.colorPaleBlue)

    private val pref by lazy { MainPref(this, PREF_NAME) }
    private var oneDrinkAmount = 0.0f
    private val drinkDialog = DrinkDialog()

    private val cupLotties =
            listOf("cup1.json", "cup2.json", "cup3.json", "cup4.json", "cup5.json")

    private var timerService: TimerService? = null
    private val onDayChangeCallback = {
        pref.intake = 0f
        runOnUiThread {
            updateCupLottie()
            initInfo()
        }
        Unit
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
        window.statusBarColor = statusBarColor
        oneDrinkAmount = String.format("%.1f", pref.onceDrinkAmount).toFloat()
        initInfo()
        updateCupLottie()

        binding.drinkBtn.setOnClickListener { drinkDialog.show(supportFragmentManager, null) }
        binding.arisuWaterSpotBtn.setOnClickListener { startActivity<MapActivity>() }
        binding.arisuStampBtn.setOnClickListener {

        }
        binding.arisuInfoBtn.setOnClickListener {

        }
        drinkDialog.drinkCallback = onDrinkCallback()
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
        binding.figureStamp.text = htmlText(R.string.figure_stamp, pref.stamp)
    }

    private fun onDrinkCallback(): () -> Unit = {
        pref.intake += oneDrinkAmount
        initInfo()
        updateCupLottie()
    }
}

class DrinkDialog : BaseDialogFragment<DialogDrinkBinding>() {

    override val resourceId = R.layout.dialog_drink
    var drinkCallback: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener { dismiss() }
        binding.qrBtn.setOnClickListener { }
        binding.drinkBtn.setOnClickListener {
            drinkCallback?.invoke()
            dismiss()
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
    var stamp by intPreference("stamp", 0)

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