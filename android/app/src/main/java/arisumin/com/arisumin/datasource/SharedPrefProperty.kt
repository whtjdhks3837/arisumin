package arisumin.com.arisumin.datasource

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


open class PreferenceModel(context: Context, name: String) {

    val pref: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    protected fun stringPreference(key: String, default: String?) =
            StringSharedPrefProperty(key, default)

    protected fun intPreference(key: String, default: Int = -1) =
            IntSharedPrefProperty(key, default)

    protected fun booleanPreference(key: String, default: Boolean = false) =
            BooleanSharedPrefProperty(key, default)
}

class StringSharedPrefProperty(
        private val key: String,
        private val default: String? = null
) : ReadWriteProperty<PreferenceModel, String?> {
    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): String? {
        return thisRef.pref.getString(key, default)
    }

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: String?) {
        thisRef.pref.edit().putString(key, value).apply()
    }
}

class IntSharedPrefProperty(
        private val key: String,
        private val default: Int = -1
) : ReadWriteProperty<PreferenceModel, Int> {
    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Int {
        return thisRef.pref.getInt(key, default)
    }

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Int) {
        thisRef.pref.edit().putInt(key, value).apply()
    }
}

class BooleanSharedPrefProperty(
        private val key: String,
        private val default: Boolean = false
) : ReadWriteProperty<PreferenceModel, Boolean> {
    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Boolean {
        return thisRef.pref.getBoolean(key, default)
    }

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Boolean) {
        thisRef.pref.edit().putBoolean(key, value).apply()
    }
}