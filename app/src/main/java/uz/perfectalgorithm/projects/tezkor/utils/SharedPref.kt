package uz.perfectalgorithm.projects.tezkor.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPref(context: Context) {

    var language: Boolean
        set(value) = mySharedPref.edit().putBoolean("device_token", value).apply()
        get() = mySharedPref.getBoolean("device_token", false)

    var isTrue: Boolean
        set(value) = mySharedPref.edit().putBoolean("device_token", value).apply()
        get() = mySharedPref.getBoolean("device_token", false)

    var startTime: String?
        get() = mySharedPref.getString("user", null)
        set(value) = mySharedPref.edit {
            if (value != null) {
                this.putString("user", value)
            }
        }

    var endTime: String?
        get() = mySharedPref.getString("use1r", null)
        set(value) = mySharedPref.edit {
            if (value != null) {
                this.putString("use1r", value)
            }
        }

    private var mySharedPref: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

}