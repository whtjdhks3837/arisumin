package arisumin.com.arisumin

import android.app.Activity
import android.content.Context
import android.util.Log
import android.util.TypedValue

infix fun Activity.log(msg: String) = Log.i(this::class.java.simpleName, msg)

fun toDp(context: Context, size: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, size, context.resources.displayMetrics
).toInt()
