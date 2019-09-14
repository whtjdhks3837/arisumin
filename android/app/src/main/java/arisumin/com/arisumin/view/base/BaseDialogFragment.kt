package arisumin.com.arisumin.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {

    abstract val resourceId: Int

    lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?) =
            DataBindingUtil.inflate<T>(inflater, resourceId, container, false)
                    .apply {
                        binding = this
                    }.root
}