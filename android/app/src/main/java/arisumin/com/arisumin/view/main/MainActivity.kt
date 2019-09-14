package arisumin.com.arisumin.view.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import arisumin.com.arisumin.startActivity
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.base.BaseDialogFragment
import arisumin.com.arisumin.view.map.MapActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val DRINK_PERIOD = 7
    }

    override val resourceId = R.layout.activity_main
    private val statusBarColor by bindColor(R.color.colorPaleBlue)

    private val pref by lazy { MainPref(this, PREF_NAME) }
    private var oneDrinkAmount = 0.0f
    private val drinkDialog = DrinkDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = statusBarColor
        oneDrinkAmount = getOneDayRecommend().toFloat() / DRINK_PERIOD
        initInfo()
        onDrinkClick()
        onWaterSpotClick()
        onStampClick()
        onInfoClick()
        drinkDialog.drinkCallback = onDrinkCallback()
    }

    private fun initInfo() {
        binding.info.text = getString(R.string.main_my_info, pref.name)
        binding.figureIntake.text = htmlText(R.string.figure_intake, getIntake())
        binding.figureRecommend.text = htmlText(R.string.figure_recommend, getOneDayRecommend())
        binding.figureGoal.text = htmlText(R.string.figure_goal, getGoal())
        binding.figureStamp.text = htmlText(R.string.figure_stamp, getStamp())
    }

    private fun getIntake() = String.format("%.1f", pref.intake)

    private fun getOneDayRecommend() = String.format("%.1f", pref.weight * 0.03)

    private fun getGoal(): Int {
        val intake = getIntake().toDouble()
        if (intake <= 0.0) {
            return 0
        }
        val recommend = getOneDayRecommend().toDouble()
        return ((intake / recommend) * 100).toInt()
    }

    private fun getStamp() = pref.stamp

    private fun onDrinkClick() = binding.drinkBtn.setOnClickListener {
        drinkDialog.show(supportFragmentManager, null)
    }

    private fun onWaterSpotClick() = binding.arisuWaterSpotBtn.setOnClickListener {
        startActivity<MapActivity>()
    }

    private fun onStampClick() = binding.arisuStampBtn.setOnClickListener {

    }

    private fun onInfoClick() = binding.arisuInfoBtn.setOnClickListener {

    }

    private fun onDrinkCallback(): () -> Unit = {
        pref.intake += oneDrinkAmount
        binding.figureIntake.text = htmlText(R.string.figure_intake, getIntake())
        binding.figureGoal.text = htmlText(R.string.figure_goal, getGoal())
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
    var name by stringPreference("name", null)
    var weight by intPreference("weight", 0)
    var intake by floatPreference("intake", 0.0f)
    var stamp by intPreference("stamp", 0)
}