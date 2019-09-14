package arisumin.com.arisumin.controller

import arisumin.com.arisumin.model.WaterSpot
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker


object MarkerManager {

    fun createMarkers(waterSpots: List<WaterSpot>, callback: (List<Marker>) -> Unit) =
            Thread {
                val makers = mutableListOf<Marker>()
                waterSpots.forEach {
                    makers += Marker().apply {
                        position = LatLng(it.lat, it.lng)
                        tag = it.tmp
                    }
                }
                callback.invoke(makers)
            }.start()

    fun getDistance(start: LatLng, goal: LatLng): Int {
        val theta = start.longitude - goal.longitude
        var dist = Math.sin(deg2rad(start.latitude)) * Math.sin(deg2rad(goal.latitude)) +
                Math.cos(deg2rad(start.latitude)) * Math.cos(deg2rad(goal.latitude)) *
                Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        dist *= 1609.344
        return dist.toInt()
    }

    private fun deg2rad(deg: Double) = (deg * Math.PI / 180.0)

    private fun rad2deg(rad: Double) = (rad * 180 / Math.PI)
}