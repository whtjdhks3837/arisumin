package arisumin.com.arisumin.util

import android.content.res.Resources
import android.content.Context
import android.text.Html
import android.text.Spanned

class ResourceUtil(private val context: Context) {

    private val resources: Resources by lazy {
        context.resources
    }

    fun stringToResourceId(text: String, context: Context): Int {
        var id = resources.getIdentifier(text, "drawable", context.packageName)
        return id
    }

    fun convertHtml(id: Int, format: String? = null): Spanned {
        val text = format?.let {
            resources.getString(id).replace("{}", format)
        } ?: let {
            resources.getString(id)
        }
        return Html.fromHtml(text)
    }
}