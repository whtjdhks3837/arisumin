package arisumin.com.arisumin.util

import android.content.res.Resources
import android.content.Context
import android.text.Html
import android.text.Spanned

class ResourceUtil(private val context:Context) {

    private val resources : Resources by lazy{
        context.resources
    }

    fun StringToResourceId(text: String, context: Context): Int {
        var id = resources.getIdentifier(text, "drawable", context.packageName)
        return id
    }

    fun ConvertHtml(id: Int, format: String? = null): Spanned {
        var text: String = ""

        if (format == null) {
            text = resources.getString(id)
        } else if (format != null) {
            text = resources.getString(id).replace("{}", format)

        }

        return Html.fromHtml(text)
    }
}