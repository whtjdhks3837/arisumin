package arisumin.com.arisumin.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.print.PrintAttributes
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.ActivityNewStampBinding
import arisumin.com.arisumin.databinding.ViewCouponBinding
import arisumin.com.arisumin.databinding.ViewStampBinding
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.model.Coupon
import arisumin.com.arisumin.model.Stamp
import arisumin.com.arisumin.model.StampCoupon
import arisumin.com.arisumin.util.ConvertUtil
import arisumin.com.arisumin.util.ResourceUtil
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.stamp.*

class NewStampAcitivity : BaseActivity<ActivityNewStampBinding>() {

    override val resourceId: Int = R.layout.activity_new_stamp

    private val couponPref by lazy { CouponPref(this, "coupon_info") }

    private var couponList = mutableListOf<Coupon>()

    private val testCouponString: String by lazy {
        "0/img_2_pro/barcode/2% 아쿠아 340ml/2019.09.18./CU"
    }

    private val resources: ResourceUtil by lazy{
        ResourceUtil(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO sharedPreference 전처리
        //couponPref.list.forEach {
        // // index/giftResourceId/barcodeResourceId/giftName/availableDate
        //var temp = it.split("/")
        //Coupon(temp[1].toInt(), temp[2].toInt(), temp[3], temp[4])
        //}

        var temp = testCouponString.split("/")

        couponList.apply {
            add(Coupon(
                    resources.StringToResourceId(temp[1], this@NewStampAcitivity),
                    resources.StringToResourceId(temp[2], this@NewStampAcitivity),
                    temp[3],
                    temp[4],
                    temp[5])
            )
        }

        //Remainder & Gift Notice
        binding.remainderNoticeText.text = resources.ConvertHtml(R.string.remainder_notice, "4")
        binding.giftNoticeText.text = resources.ConvertHtml(R.string.gift_notice, "이온 음료")

        //Count
        binding.stampCount.text = resources.ConvertHtml(R.string.stamp_count, "6")
        binding.couponCount.text = resources.ConvertHtml(R.string.coupon_count, "2")

        //Recycler
        binding.stampCouponRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NewStampAcitivity, LinearLayoutManager.HORIZONTAL, false)

            adapter = StampCouponRecyclerAdapter().apply {

                addItem(Stamp(5))
                couponList.forEach {
                    addItem(it)
                }
                this@apply.notifyDataSetChanged()
            }

            addItemDecoration(StampCouponRecyclerDecoration(0))
        }

        //CouponNotice
        binding.couponNoticeText1.text = resources.ConvertHtml(R.string.coupon_notice_1)
        binding.couponNoticeText2.text = resources.ConvertHtml(R.string.coupon_notice_2)
        binding.couponNoticeText3.text = resources.ConvertHtml(R.string.coupon_notice_3)
    }
}

class StampCouponRecyclerDecoration(private var dp: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = ConvertUtil().dpToPx(dp).toInt();

    }
}

class StampCouponRecyclerAdapter : RecyclerView.Adapter<StampCouponRecyclerAdapter.ItemViewHolder>() {
    private lateinit var context: Context
    private var itemList: MutableList<StampCoupon> = mutableListOf()

    private lateinit var viewBinding: ViewDataBinding
    private lateinit var viewHolder: ItemViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context

        var inflater = LayoutInflater.from(context)

        //카드뷰 동적 margin 및 width
        var stampParams = LinearLayoutCompat.LayoutParams(
                ConvertUtil().percentToPxWidth(0.86F).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT).apply {
            leftMargin = ConvertUtil().percentToPxWidth(0.07F).toInt()
        }

        var couponParams = LinearLayoutCompat.LayoutParams(
                ConvertUtil().percentToPxWidth(0.86F).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT).apply {
            leftMargin = ConvertUtil().percentToPxWidth(0.03F).toInt()
        }

        if (viewType == 0) {
            //Stamp
            var binding = DataBindingUtil.inflate<ViewStampBinding>(inflater, R.layout.view_stamp, parent, false)
            binding.stampWrapper.apply {
                layoutParams = stampParams
            }
            viewHolder = StampViewHolder(binding as ViewStampBinding, context)
        } else if (viewType == 1) {
            //Coupon

            var binding = DataBindingUtil.inflate<ViewCouponBinding>(inflater, R.layout.view_coupon, parent, false)
            binding.couponWrapper.apply {
                layoutParams = couponParams
            }
            viewHolder = CouponViewHolder(binding as ViewCouponBinding, context)
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = position

        if (type != 0)
            type /= type

        return type
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var viewType = getItemViewType(position)

        if (viewType == 0) {
            holder.bindData(itemList[position])

        } else if (viewType == 1) {
            holder.bindData(itemList[position])

        }
    }

    fun addItem(item: StampCoupon) {
        itemList.add(item)
    }

    abstract class ItemViewHolder(private var binding: ViewDataBinding, private var context: Context) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bindData(data: StampCoupon)
    }

    class StampViewHolder(private var binding: ViewStampBinding, private var context: Context) : ItemViewHolder(binding, context) {
        override fun bindData(data: StampCoupon) {
            val stampPref by lazy { StampPref(context, "stamp_info") }
            //TODO sharedPreference
            //var tempCount: Int = stampPref.count
            var tempCount: Int = 5
            var endCount: Int = 9
            for (x in 0..endCount) {
                binding.couponGrid.apply {
                    var resource: gridResource = stampResource(false)
                    if (x < tempCount) resource = stampResource(true)
                    else if (x == endCount) resource = gitfResource(false)

                    var tempImageView = ImageView(context)
                    val params = GridLayout.LayoutParams(
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.CENTER, 1f),
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.CENTER, 1f))

                    tempImageView.layoutParams = params.apply {
                        if (x < 5) bottomMargin = ConvertUtil().dpToPx(30).toInt()
                    }
                    tempImageView.background = ContextCompat.getDrawable(context, resource.currentResourceId)

                    binding.couponGrid.addView(tempImageView)
                }
            }
        }
    }

    class CouponViewHolder(private var binding: ViewCouponBinding, private var context: Context) : ItemViewHolder(binding, context) {
        override fun bindData(data: StampCoupon) {
            var resources = ResourceUtil(context)

            if (data is Coupon) {
                binding.couponImage.background = ContextCompat.getDrawable(context, data.couponResourceId)
                binding.couponName.text = resources.ConvertHtml(R.string.coupon_name, data.couponName)
                binding.couponValidateDate.text = resources.ConvertHtml(R.string.coupon_validate_date, data.availableDate)
                binding.couponUsingPlace.text = resources.ConvertHtml(R.string.coupon_using_place, data.usingPlace)
            }
        }

    }

}

class StampPref(context: Context, name: String) : PreferenceModel(context, name) {
    var count: Int by intPreference("count", 0)
}

class CouponPref(context: Context, name: String) : PreferenceModel(context, name) {
    var list: Set<String> by stringSetPreference("list", emptySet())
}
