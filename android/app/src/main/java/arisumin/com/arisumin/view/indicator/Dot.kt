package arisumin.com.arisumin.view.indicator

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import arisumin.com.arisumin.R

class Dot(context: Context, highlighting: Boolean = false) : View(context) {
    val dotDefaultResource : Int = R.drawable.ic_indicator_graycircle
    val dotHighlightResource : Int = R.drawable.ic_indicator_bluecircle
    val dotNormal : Drawable? by lazy {
       ContextCompat.getDrawable(context, dotDefaultResource)
    }
    val dotHighLight : Drawable? by lazy {
        ContextCompat.getDrawable(context, dotHighlightResource)
    }

    init{
        setState(highlighting)
    }

    fun setState(highlighting: Boolean = false){
        if(highlighting)
            setBackground(dotHighLight)
        else
            setBackground(dotNormal)
    }
}