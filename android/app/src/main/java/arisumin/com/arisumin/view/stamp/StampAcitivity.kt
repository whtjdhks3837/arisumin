package arisumin.com.arisumin.view.stamp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arisumin.com.arisumin.R
import arisumin.com.arisumin.bindColor
import arisumin.com.arisumin.databinding.ActivityStampBinding
import arisumin.com.arisumin.databinding.DialogBarcodeBinding
import arisumin.com.arisumin.databinding.ViewCouponBinding
import arisumin.com.arisumin.databinding.ViewStampBinding
import arisumin.com.arisumin.datasource.PREF_NAME
import arisumin.com.arisumin.datasource.PreferenceModel
import arisumin.com.arisumin.model.Coupon
import arisumin.com.arisumin.model.Stamp
import arisumin.com.arisumin.model.StampCoupon
import arisumin.com.arisumin.util.ConvertUtil
import arisumin.com.arisumin.util.ResourceUtil
import arisumin.com.arisumin.view.base.BaseActivity
import arisumin.com.arisumin.view.base.BaseDialogFragment

class StampAcitivity : BaseActivity<ActivityStampBinding>() {

    override val resourceId: Int = R.layout.activity_stamp
    private val statusBarColor by bindColor(R.color.colorSkyBlue)
    private val barcodeDialog = BarcodeDialog()

    private val pref by lazy { MainPref(this, PREF_NAME) }

    private val couponList = mutableListOf<Coupon>()

    private val resources: ResourceUtil by lazy {
        ResourceUtil(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = statusBarColor

        pref.couponList.forEach {
            var temp = it.split("/")
            couponList.apply {
                add(Coupon(
                        resources.stringToResourceId(temp[1], this@StampAcitivity),
                        resources.stringToResourceId(temp[2], this@StampAcitivity),
                        temp[3],
                        temp[4],
                        temp[5])
                )
            }
        }

        //Remainder & Gift Notice
        binding.remainderNoticeText.text = resources.convertHtml(R.string.remainder_notice, (10 - pref.stampCount).toString())
        binding.giftNoticeText.text = resources.convertHtml(R.string.gift_notice, "이온 음료")

        //Count
        binding.stampCount.text = resources.convertHtml(R.string.stamp_count, pref.stampCount.toString())
        binding.couponCount.text = resources.convertHtml(R.string.coupon_count, pref.couponList.size.toString())

        //Recycler
        binding.stampCouponRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@StampAcitivity, LinearLayoutManager.HORIZONTAL, false)

            adapter = StampCouponRecyclerAdapter().apply {
                onPropagationCallback = { showBarcodeDialog() }

                addItem(Stamp(5))
                couponList.forEach {
                    addItem(it)
                }
                notifyDataSetChanged()
            }

            addItemDecoration(StampCouponRecyclerDecoration(0))
        }

        //CouponNotice
        binding.couponNoticeText1.text = resources.convertHtml(R.string.coupon_notice_1)
        binding.couponNoticeText2.text = resources.convertHtml(R.string.coupon_notice_2)
        binding.couponNoticeText3.text = resources.convertHtml(R.string.coupon_notice_3)

        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }
    }

    fun showBarcodeDialog() {
        barcodeDialog.show(supportFragmentManager, null)
    }
}

class StampCouponRecyclerDecoration(private var dp: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = ConvertUtil.dpToPx(dp).toInt();
    }
}

class StampCouponRecyclerAdapter : RecyclerView.Adapter<StampCouponRecyclerAdapter.ItemViewHolder>() {
    private lateinit var context: Context
    private val itemList: MutableList<StampCoupon> = mutableListOf()

    private lateinit var viewHolder: ItemViewHolder

    private val VIEW_TYPE_STAMP = 0
    private val VIEW_TYPE_COUPON = 1
    private val VIEW_TYPE_LAST = 2

    var onPropagationCallback: (() -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context

        var inflater = LayoutInflater.from(context)

        //카드뷰 동적 margin 및 width
        var stampParams = LinearLayoutCompat.LayoutParams(
                ConvertUtil.percentToPxWidth(0.86F).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT).apply {
            leftMargin = ConvertUtil.percentToPxWidth(0.07F).toInt()
            gravity = Gravity.CENTER
        }

        var couponParams = LinearLayoutCompat.LayoutParams(
                ConvertUtil.percentToPxWidth(0.86F).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT).apply {
            leftMargin = ConvertUtil.percentToPxWidth(0.03F).toInt()
        }

        var couponLastParams = LinearLayoutCompat.LayoutParams(
                ConvertUtil.percentToPxWidth(0.86F).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT).apply {
            leftMargin = ConvertUtil.percentToPxWidth(0.03F).toInt()
            rightMargin = ConvertUtil.percentToPxWidth(0.07F).toInt()
        }

        when (viewType) {
            VIEW_TYPE_STAMP -> {
                var binding = DataBindingUtil.inflate<ViewStampBinding>(inflater, R.layout.view_stamp, parent, false)
                binding.stampWrapper.apply {
                    layoutParams = stampParams
                }
                viewHolder = StampViewHolder(binding as ViewStampBinding, context)
            }
            else -> {
                var binding = DataBindingUtil.inflate<ViewCouponBinding>(inflater, R.layout.view_coupon, parent, false)
                binding.couponWrapper.apply {
                    layoutParams = if (viewType == VIEW_TYPE_COUPON) couponParams else couponLastParams
                }
                viewHolder = CouponViewHolder(binding as ViewCouponBinding, context).apply {
                    onBarcodeCallback = {
                        onPropagationCallback?.invoke()
                    }
                }
            }
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_STAMP
            itemList.size - 1 -> VIEW_TYPE_LAST
            else -> VIEW_TYPE_COUPON
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    fun addItem(item: StampCoupon) {
        itemList.add(item)
    }

    abstract class ItemViewHolder(private var binding: ViewDataBinding, private var context: Context) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bindData(data: StampCoupon)
    }

    class StampViewHolder(private var binding: ViewStampBinding, private var context: Context) : ItemViewHolder(binding, context) {
        override fun bindData(data: StampCoupon) {
            val pref by lazy { MainPref(context, PREF_NAME) }
            val couponCount = pref.stampCount

            val STAMP_BLUE_RESOURCE_ID = R.drawable.img_stamp_blue

            for (i in 0 until couponCount) {
                binding.couponGrid.getChildAt(i).apply {
                    background = ContextCompat.getDrawable(context, STAMP_BLUE_RESOURCE_ID)
                }
            }
        }
    }

    class CouponViewHolder(private var binding: ViewCouponBinding, private var context: Context) : ItemViewHolder(binding, context) {
        var onBarcodeCallback: (() -> Unit)? = null

        init {
            this.itemView.setOnClickListener {
                onBarcodeCallback?.invoke()
            }
        }

        override fun bindData(data: StampCoupon) {
            var resources = ResourceUtil(context)

            if (data is Coupon) {
                binding.couponImage.background = ContextCompat.getDrawable(context, data.couponResourceId)
                binding.couponName.text = resources.convertHtml(R.string.coupon_name, data.couponName)
                binding.couponValidateDate.text = resources.convertHtml(R.string.coupon_validate_date, data.availableDate)
                binding.couponUsingPlace.text = resources.convertHtml(R.string.coupon_using_place, data.usingPlace)
            }
        }
    }
}

class MainPref(context: Context, name: String) : PreferenceModel(context, name) {
    var stampCount by intPreference("stampCount", 0)
    var couponList: MutableSet<String> by stringSetPreference("couponList", mutableSetOf())
}

class BarcodeDialog : BaseDialogFragment<DialogBarcodeBinding>() {
    override val resourceId: Int = R.layout.dialog_barcode
}