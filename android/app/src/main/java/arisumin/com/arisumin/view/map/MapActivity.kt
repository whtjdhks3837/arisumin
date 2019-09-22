package arisumin.com.arisumin.view.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import arisumin.com.arisumin.R
import arisumin.com.arisumin.controller.MarkerManager
import arisumin.com.arisumin.databinding.ActivityMapBinding
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.model.WaterSpot
import arisumin.com.arisumin.model.WaterSpots
import arisumin.com.arisumin.readJsonFromAsset
import arisumin.com.arisumin.toDp
import arisumin.com.arisumin.view.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.gson.Gson
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class MapActivity : BaseActivity<ActivityMapBinding>() {

    companion object {
        const val REQ_LOCATION = 0x01
        const val DEFAULT_BOTTOM_SHEET_HEIGHT = 164f
        const val METER_PER_MIN = 70
        const val MIN_PER_KCAL = 3
    }

    override val resourceId: Int = R.layout.activity_map
    private val pref by lazy { MapPref(this, PREF_NAME) }

    private val mapFragment: MapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
                }
    }

    private val fusedLocationSource by lazy { FusedLocationSource(this, REQ_LOCATION) }
    private var naverMap: NaverMap? = null
    private var selectedMarker: Marker? = null

    private val iconMarkerOff = OverlayImage.fromResource(R.drawable.mappin_off)
    private val iconMarkerOn = OverlayImage.fromResource(R.drawable.mappin_on)

    private val waterSpots by lazy {
        Gson().fromJson(readJsonFromAsset("arisu.json"), WaterSpots::class.java)
    }
    private val measureInfos by lazy {
        listOf(
                WaterMeasure(getString(R.string.ntu_info), "탁도\n(NTU)", 5f, Pair(0f, 0.5f)),
                WaterMeasure(getString(R.string.chlorine_info), "잔류염소\n(Mg/L)", 5f, Pair(0.1f, 4f)),
                WaterMeasure(getString(R.string.pH_info), "pH", 10f, Pair(5.8f, 8.5f)))
    }

    private val bottomSheet by lazy { binding.bottomSheet }
    private val measureBtnGroup by lazy { binding.bottomSheet.measureBtnGroup }
    private val measureDetail by lazy { bottomSheet.measureDetail }
    private var graphAreaWidth = 0

    private val tmpValues = listOf(0.06f, 3f, 7.1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        measureDetail.graphArea.post { graphAreaWidth = measureDetail.graphArea.width }

        initMap()
        initBottomSheet()

        binding.back.setOnClickListener { finish() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray) {
        if (fusedLocationSource.onRequestPermissionsResult(
                        requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initMap() = mapFragment.getMapAsync { naverMap ->
        this.naverMap = naverMap.apply {
            locationSource = fusedLocationSource
            locationTrackingMode = LocationTrackingMode.Follow
        }
        MarkerManager.createMarkers(waterSpots.list) { markers ->
            drawMarkers(naverMap, markers)
        }
    }

    private fun initBottomSheet() = with(BottomSheetBehavior.from(bottomSheet.root)) {
        peekHeight = toDp(this@MapActivity, DEFAULT_BOTTOM_SHEET_HEIGHT)
        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bs: View, newState: Int) {
                when (newState) {
                    STATE_EXPANDED -> {
                        bottomSheet.btnOpenClose.setImageResource(R.drawable.bt_map_up)
                    }
                    STATE_COLLAPSED -> {
                        bottomSheet.btnOpenClose.setImageResource(R.drawable.bt_map_down)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun drawMarkers(naverMap: NaverMap, markers: List<Marker>) = runOnUiThread {
        markers.forEach {
            it.icon = iconMarkerOff
            it.map = naverMap
            it.setOnClickListener(onMarkerClick)
        }
    }

    private val onMarkerClick: (Overlay) -> Boolean = {
        if (it is Marker) {
            showMarkerInfoView()
            if (!isSelectedMarker(it)) {
                val waterSpot = it.tag as WaterSpot
                naverMap?.locationOverlay?.position?.let { pos ->
                    MarkerManager.getDistance(pos, it.position).run {
                        bindMapInfo(waterSpot, this)
                    }
                }
                measureBtnGroup.setOnCheckedChangeListener { _, id ->
                    val index = measureBtnGroup.indexOfChild(findViewById(id))
                    bindGraph(waterSpot, index)
                }
                measureBtnGroup.check(R.id.ntu_btn)
                selectedMarker?.icon = iconMarkerOff
                selectedMarker = it.apply { icon = iconMarkerOn }
            }
        }
        false
    }

    private fun showMarkerInfoView() {
        if (binding.nonClickMarkerView.visibility == View.VISIBLE) {
            binding.nonClickMarkerView.visibility = View.GONE
            binding.markerInfo.visibility = View.VISIBLE
        }
    }

    private fun bindMapInfo(waterSpot: WaterSpot, distance: Int) {
        pref.index = waterSpot.index
        val workingTime = distance / METER_PER_MIN
        val kcal = workingTime * MIN_PER_KCAL
        bottomSheet.workingTime.text = getString(R.string.map_info_working_min, workingTime)
        bottomSheet.kcal.text = getString(R.string.map_info_kcal, kcal)
        bottomSheet.addressSimple.text = getString(R.string.map_info_address_simple, waterSpot.name)
        bottomSheet.address.text = getString(R.string.map_info_address_simple, waterSpot.address)
        bottomSheet.distance.text = getString(R.string.map_info_distance, distance)
        bottomSheet.visitCount.text = getString(R.string.map_bottom_sheet_visit, pref.visitCount)
    }

    private fun bindGraph(waterSpot: WaterSpot, index: Int) {
        val safeArea = "${measureInfos[index].safeArea.first} ~ ${measureInfos[index].safeArea.second}"
        measureDetail.info.text = measureInfos[index].info
        measureDetail.species.text = measureInfos[index].name
        measureDetail.safeArea.text = safeArea
        measureDetail.measureValue.text = tmpValues[index].toString()
        drawMeasureBar(tmpValues[index] / measureInfos[index].maxValue)
    }

    private fun drawMeasureBar(barWidthPer: Float) {
        measureDetail.bar.layoutParams.width = (graphAreaWidth * barWidthPer).toInt()
        measureDetail.bar.requestLayout()
    }

    private fun isSelectedMarker(marker: Marker) = selectedMarker === marker
}

class MapPref(context: Context, name: String) : PreferenceModel(context, name) {
    var index = -1
    val visitCount by intPreference("index $index", 0)
}

data class WaterMeasure(
        val info: String,
        val name: String,
        val maxValue: Float,
        val safeArea: Pair<Float, Float>
)