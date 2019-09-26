package arisumin.com.arisumin.view.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

abstract class BaseQR {
    abstract val parent: Any
    protected val qr: IntentIntegrator by lazy {
        when (parent) {
            is Activity -> {
                IntentIntegrator(parent as Activity)
            }
            is Fragment -> {
                try {
                    IntentIntegrator.forSupportFragment(parent as Fragment)
                } catch (e: ClassCastException) {
                    IntentIntegrator.forFragment(parent as android.app.Fragment?)
                }
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    abstract fun customize()

    fun start() {
        qr.initiateScan()
    }
}
