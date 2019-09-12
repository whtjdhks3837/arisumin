package arisumin.com.arisumin.view

import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.ActivityStampBinding
import arisumin.com.arisumin.databinding.ViewCouponBinding
import arisumin.com.arisumin.databinding.ViewStampBinding
import arisumin.com.arisumin.view.base.BaseActivity



abstract class gridResource() {
    abstract val normalResourceId: Int
    abstract val highlightResourceId: Int
    open val currentResourceId: Int by lazy {
        if(isActivated)
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

class StampActivity : BaseActivity<ActivityStampBinding>() {

    override val resourceId: Int = R.layout.activity_stamp

    private val dataBindingList: List<ViewDataBinding> by lazy {
        listOf(
                DataBindingUtil.inflate<ViewStampBinding>(LayoutInflater.from(this), R.layout.view_stamp, null, false),
                DataBindingUtil.inflate<ViewCouponBinding>(LayoutInflater.from(this), R.layout.view_coupon, null, false)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewPagerStampActivity.apply {
            adapter = StampPagerAdapter(rooting(dataBindingList))
        }
    }

    fun rooting(dataBindingList: List<ViewDataBinding>): List<View> {
        var viewList = mutableListOf<View>()

        dataBindingList.forEach {
            viewList.add(mutating(it).root)
        }

        return viewList
    }

    fun mutating(binding: ViewDataBinding): ViewDataBinding {
        if (binding is ViewStampBinding) {
            //지금까지 받은 스탬프 갯수 가져오기
            var tempCount: Int = 5
            var endCount: Int = 9
            for (x in 0..endCount) {
                binding.couponGrid.apply {
                    var resource: gridResource = stampResource(false)
                    if (x < tempCount)
                        resource = stampResource(true)
                    else if (x == endCount)
                        resource = gitfResource(false)

                    var tempImageView = ImageView(this@StampActivity)
                    val params = GridLayout.LayoutParams(
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.CENTER, 1f),
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.CENTER, 1f))



                    tempImageView.layoutParams = params
                    tempImageView.background = ContextCompat.getDrawable(this@StampActivity, resource.currentResourceId)

                    binding.couponGrid.addView(tempImageView)
                }
            }
        } else if (binding is ViewCouponBinding) {

        }

        return binding
    }
}

class StampPagerAdapter(private val viewList: List<View>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = viewList[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return viewList.size
    }

}