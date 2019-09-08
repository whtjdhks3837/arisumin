package arisumin.com.arisumin.model

data class WaterSpot(
        val lat: Double,
        val lng: Double,
        // 수질정보 등이 들어갈 예정
        val tmp: String = ""
)