package arisumin.com.arisumin.model

sealed class StampCoupon

data class Coupon(val couponResourceId: Int, val barcodeResourceId: Int, val couponName: String, val availableDate: String, val usingPlace: String) : StampCoupon()

data class Stamp(val drinkCount: Int) : StampCoupon()

