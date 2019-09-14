package arisumin.com.arisumin.view.map

import android.os.Bundle
import android.view.View
import arisumin.com.arisumin.R
import arisumin.com.arisumin.controller.MarkerManager
import arisumin.com.arisumin.databinding.ActivityMapBinding
import arisumin.com.arisumin.model.WaterSpot
import arisumin.com.arisumin.toDp
import arisumin.com.arisumin.view.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private val waterSpots = listOf(
            WaterSpot(37.388771, 126.958036, "a"), WaterSpot(37.390297, 126.956759, "b"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMap()
        fusedLocationSource = FusedLocationSource(this, REQ_LOCATION)
        binding.bottomSheet.let {
            BottomSheetBehavior.from(it.root).peekHeight = toDp(this, DEFAULT_BOTTOM_SHEET_HEIGHT)
        }
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
        MarkerManager.createMarkers(waterSpots) { markers ->
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
                naverMap?.locationOverlay?.position?.let { pos ->
                    MarkerManager.getDistance(pos, it.position).run {
                        bindMapInfo(it, this)
                    }
                }
                selectedMarker?.icon = iconMarkerOff
                selectedMarker = it.apply { icon = iconMarkerOn }
            }
        }
        false
    }

    private fun bindMapInfo(marker: Marker, distance: Int) {
        binding.bottomSheet.addressSimple.text =
                getString(R.string.map_info_address_simple, marker.tag)
        binding.bottomSheet.distance.text =
                getString(R.string.map_info_distance, distance)
    }

    private fun showMarkerInfoView() {
        if (binding.nonClickMarkerView.visibility == View.VISIBLE) {
            binding.nonClickMarkerView.visibility = View.GONE
            binding.markerInfo.visibility = View.VISIBLE
        }
    }

    private fun isSelectedMarker(marker: Marker) = selectedMarker === marker
}