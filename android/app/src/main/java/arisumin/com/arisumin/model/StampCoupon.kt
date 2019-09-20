package arisumin.com.arisumin.model

sealed class StampCoupon()

data class Coupon(var couponResourceId: Int, var barcodeResourceId: Int, var couponName: String, var availableDate: String, var usingPlace: String) : StampCoupon()

data class Stamp(var drinkCount: Int) : StampCoupon()

