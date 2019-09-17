package arisumin.com.arisumin.view.stamp

import arisumin.com.arisumin.R

abstract class gridResource() {
    abstract val normalResourceId: Int
    abstract val highlightResourceId: Int
    open val currentResourceId: Int by lazy {
        if (isActivated)
            highlightResourceId
        else
            normalResourceId
    }
    open var isActivated: Boolean = false
}

class stampResource(override var isActivated: Boolean) : gridResource() {
    override val normalResourceId: Int = R.drawable.img_stamp_gray
    override val highlightResourceId: Int = R.drawable.img_stamp_blue
}

class gitfResource(override var isActivated: Boolean) : gridResource() {
    override val normalResourceId: Int = R.drawable.img_stamp_gift
    override val highlightResourceId: Int = R.drawable.img_stamp_gift
}