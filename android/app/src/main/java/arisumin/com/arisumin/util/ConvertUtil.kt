package arisumin.com.arisumin.util

import android.content.res.Resources
import android.util.DisplayMetrics

object ConvertUtil{
    private var displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

    fun dpToPx(dp: Int): Float {
        return (dp * displayMetrics.density)
    }

    fun pxToDp(px: Int): Float {
        return (px / displayMetrics.density)
    }

    fun percentToPxWidth(percent: Float): Float {
        return dpToPx((displayMetrics.widthPixels / displayMetrics.density).toInt()) * percent
    }

    fun percentToPxHeight(percent: Float): Float {
        return (displayMetrics.heightPixels / displayMetrics.density) * percent
    }

}