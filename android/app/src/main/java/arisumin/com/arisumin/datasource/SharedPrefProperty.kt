package arisumin.com.arisumin.datasource

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


open class PreferenceModel(context: Context, name: String) {

    val pref: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
const val PREF_NAME = "arisu"

open class PreferenceModel(context: Context, name: String) {

    internal val pref: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)


    protected fun stringPreference(key: String, default: String?) =
            StringSharedPrefProperty(key, default)

    protected fun intPreference(key: String, default: Int = -1) =
            IntSharedPrefProperty(key, default)

    protected fun booleanPreference(key: String, default: Boolean = false) =
            BooleanSharedPrefProperty(key, default)

    protected fun stringSetPreference(key: String, default: Set<String>) =
            StringSetSharedPrefProperty(key, default)
  
    protected fun floatPreference(key: String, default: Float = -1.0f) =
            FloatSharedPrefProperty(key, default)
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

class StringSetSharedPrefProperty(
        private val key: String,
        private val default: Set<String> = emptySet()
) : ReadWriteProperty<PreferenceModel, Set<String>>{
    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Set<String> {
        return thisRef.pref.getStringSet(key, default) as Set<String>
    }

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Set<String>) {
        return thisRef.pref.edit().putStringSet(key, value).apply()
      
class FloatSharedPrefProperty(
        private val key: String,
        private val default: Float = -1.0f
) : ReadWriteProperty<PreferenceModel, Float> {
    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Float {
        return thisRef.pref.getFloat(key, default)
    }

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Float) {
        thisRef.pref.edit().putFloat(key, value).apply()
    }
}