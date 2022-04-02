package uz.perfectalgorithm.projects.tezkor.utils.helper

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanPreference(
    private val pref: SharedPreferences,
    private val defValue: Boolean = false
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        pref.getBoolean(property.name, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        pref.edit().putBoolean(property.name, value).apply()
}

class IntPreference(
    private val pref: SharedPreferences,
    private val defValue: Int = -1
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        pref.getInt(property.name, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) =
        pref.edit().putInt(property.name, value).apply()
}

class LongPreference(
    private val pref: SharedPreferences,
    private val defValue: Long = 0L
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        pref.getLong(property.name, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) =
        pref.edit().putLong(property.name, value).apply()
}

class StringPreference(
    private val pref: SharedPreferences,
    private val defValue: String = ""
) : ReadWriteProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String =
        pref.getString(property.name, defValue) ?: ""

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) =
        pref.edit().putString(property.name, value).apply()
}

class FloatPreference(
    private val pref: SharedPreferences,
    private val defValue: Float = 0.0f
) : ReadWriteProperty<Any, Float> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Float =
        pref.getFloat(
            property.name,
            defValue
        )

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) =
        pref.edit().putFloat(property.name, value).apply()
}

class StringSetPreference(
    private val pref: SharedPreferences,
    private val defValue: Set<String> = setOf()
) : ReadWriteProperty<Any, Set<String>> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> =
        pref.getStringSet(property.name, defValue) ?: setOf()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) =
        pref.edit().putStringSet(property.name, value).apply()
}