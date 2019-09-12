package arisumin.com.arisumin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast

infix fun Activity.log(msg: String) = Log.i(this::class.java.simpleName, msg)

infix fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun toDp(context: Context, size: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, size, context.resources.displayMetrics
).toInt()

inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java), bundle)
}

inline fun <reified T : Activity> Activity.startActivityWithFinish(bundle: Bundle? = null) {
    this.startActivity<T>(bundle)
}
