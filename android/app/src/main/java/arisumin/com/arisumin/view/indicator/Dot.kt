package arisumin.com.arisumin.view.indicator

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import arisumin.com.arisumin.R

class Dot : View {
    val DotDefaultResource : Int = R.drawable.ic_indicator_graycircle
    val DotHighlightResource : Int = R.drawable.ic_indicator_bluecircle
    val DotNormal : Drawable? by lazy {
       ContextCompat.getDrawable(context, DotDefaultResource)
    }
    val DotHighLight : Drawable? by lazy {
        ContextCompat.getDrawable(context, DotHighlightResource)
    }

    constructor(context: Context, highlighting: Boolean = false) : super(context){
        setState(highlighting)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setState(highlighting: Boolean = false){
        if(highlighting)
            setBackground(DotHighLight)
        else
            setBackground(DotNormal)
    }
}