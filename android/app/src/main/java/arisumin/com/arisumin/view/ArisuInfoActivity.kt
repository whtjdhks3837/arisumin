package arisumin.com.arisumin.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.util.Log
import android.view.View
import android.view.ViewGroup
import arisumin.com.arisumin.databinding.ActivityArisuInfoBinding
import arisumin.com.arisumin.view.base.BaseActivity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import arisumin.com.arisumin.R
import arisumin.com.arisumin.databinding.FragmentArisuInfoPagerItemBinding

data class ArisuInfoData(
        val text : String,
        val imageResourceId : Int
)

class ArisuInfoActivity : BaseActivity<ActivityArisuInfoBinding>() {

    override val resourceId : Int = R.layout.activity_arisu_info
    private val text1 : String by lazy { resources.getString(R.string.arisu_info_page_1_description) }
    private val text2 : String by lazy { resources.getString(R.string.arisu_info_page_2_description) }
    private val text3 : String by lazy { resources.getString(R.string.arisu_info_page_3_description) }

    private val pageResource : List<ArisuInfoData> by lazy {
        listOf(
            ArisuInfoData(text1, R.drawable.arisu_explain_01),
            ArisuInfoData(text2, R.drawable.arisu_explain_02),
            ArisuInfoData(text3, R.drawable.arisu_explain_03))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewPagerArisuInfo.apply{
            adapter = ArisuInfoPagerAdapter(pageResource, supportFragmentManager)
            addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
                override fun onPageSelected(position: Int) {
                }
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_SETTLING){
                        //indicator update
                        binding.indicatorArisuInfo.apply {
                            currentPage = currentItem
                            updateDotState()
                        }

                        //button update
                        binding.startButtonArisuInfo.apply {
                            if(adapter!!.count-1 == currentItem)
                                visibility = View.VISIBLE
                        }
                    }
                }
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }
            })
        }

        binding.indicatorArisuInfo.apply {
            pageCount = pageResource.size
            updateDotState()
        }

    }
}

class ArisuInfoPagerAdapter(private val infoList : List<ArisuInfoData>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return ArisuInfoPagerList.newInstance(infoList[position])
    }

    override fun getCount(): Int {
        return infoList.size
    }
}

class ArisuInfoPagerList : Fragment() {
    private lateinit var binding: FragmentArisuInfoPagerItemBinding

    companion object{
        fun newInstance(info : ArisuInfoData): ArisuInfoPagerList {
            val args: Bundle = Bundle()
            args.putString("param1", info.text) //Text
            args.putInt("param2", info.imageResourceId) //ResourceID

            val InfoPagerList = ArisuInfoPagerList()
            InfoPagerList.arguments = args

            return InfoPagerList
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_arisu_info_pager_item, container, false)
        binding.textViewArisuInfoPagerItem.text = Html.fromHtml(arguments?.get("param1") as String)
        binding.imageViewArisuInfoPagerItem.setImageResource(arguments?.get("param2") as Int)
        return binding.root
    }
}