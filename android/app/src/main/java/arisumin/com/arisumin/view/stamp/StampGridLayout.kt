package arisumin.com.arisumin.view.stamp

import arisumin.com.arisumin.R

abstract class GridResource() {
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

class StampResource(override var isActivated: Boolean) : GridResource() {
    override val normalResourceId: Int = R.drawable.img_stamp_gray
    override val highlightResourceId: Int = R.drawable.img_stamp_blue
}

class GitfResource(override var isActivated: Boolean) : GridResource() {
    override val normalResourceId: Int = R.drawable.img_stamp_gift
    override val highlightResourceId: Int = R.drawable.img_stamp_gift
}