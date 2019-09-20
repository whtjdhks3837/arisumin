package arisumin.com.arisumin.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WaterSpots(
    @SerializedName("data") @Expose val list: List<WaterSpot>
)

data class WaterSpot(
        @SerializedName("index") @Expose val index: Int,
        @SerializedName("name") @Expose val name: String,
        @SerializedName("address") @Expose val address: String,
        @SerializedName("lat") @Expose val lat: Double,
        @SerializedName("long") @Expose val lng: Double
)