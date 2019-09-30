package arisumin.com.arisumin.view.base

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    abstract val resourceId: Int

    protected val binding: T by lazy { DataBindingUtil.setContentView<T>(this, resourceId) }
}