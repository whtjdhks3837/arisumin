package arisumin.com.arisumin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

infix fun Any.log(msg: String) = Log.i(this::class.java.simpleName, msg)

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

fun Activity.bindColor(@ColorRes res: Int): Lazy<Int> = lazy {
    ContextCompat.getColor(this, res)
}

fun Context.htmlText(@StringRes res: Int, vararg formatArgs: Any): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(res, *formatArgs), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(getString(res, *formatArgs))
        }

fun Context.readJsonFromAsset(fileName: String) =
        assets.open(fileName).bufferedReader().use { it.readText() }
