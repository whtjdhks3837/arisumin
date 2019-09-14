package arisumin.com.arisumin.view.map

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.*
import android.view.WindowManager.LayoutParams.*
import arisumin.com.arisumin.R
import arisumin.com.arisumin.bindColor
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
    }

    override val resourceId: Int = R.layout.activity_map
    private val pref by lazy { MapPref(this, PREF_NAME) }

    private val mapFragment: MapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
                }
    }

    private lateinit var fusedLocationSource: FusedLocationSource
    private var naverMap: NaverMap? = null
    private var selectedMarker: Marker? = null

    private val iconMarkerOff = OverlayImage.fromResource(R.drawable.mappin_off)
    private val iconMarkerOn = OverlayImage.fromResource(R.drawable.mappin_on)

    private val waterSpots by lazy {
        Gson().fromJson(readJsonFromAsset("arisu.json"), WaterSpots::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        initMap()
        fusedLocationSource = FusedLocationSource(this, REQ_LOCATION)
        binding.bottomSheet.let {
            BottomSheetBehavior.from(it.root).peekHeight = toDp(this, DEFAULT_BOTTOM_SHEET_HEIGHT)
        }
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
                selectedMarker?.icon = iconMarkerOff
                selectedMarker = it.apply { icon = iconMarkerOn }
            }
        }
        false
    }

    private fun bindMapInfo(waterSpot: WaterSpot, distance: Int) {
        pref.index = waterSpot.index
        binding.bottomSheet.addressSimple.text =
                getString(R.string.map_info_address_simple, waterSpot.name)
        binding.bottomSheet.address.text =
                getString(R.string.map_info_address_simple, waterSpot.address)
        binding.bottomSheet.distance.text =
                getString(R.string.map_info_distance, distance)
        binding.bottomSheet.visitCount.text =
                getString(R.string.map_bottom_sheet_visit, pref.visitCount)
    }

    private fun showMarkerInfoView() {
        if (binding.nonClickMarkerView.visibility == View.VISIBLE) {
            binding.nonClickMarkerView.visibility = View.GONE
            binding.markerInfo.visibility = View.VISIBLE
        }
    }

    private fun isSelectedMarker(marker: Marker) = selectedMarker === marker
}

class MapPref(context: Context, name: String) : PreferenceModel(context, name) {
    var index = -1
    val visitCount by intPreference("index $index", 0)
}