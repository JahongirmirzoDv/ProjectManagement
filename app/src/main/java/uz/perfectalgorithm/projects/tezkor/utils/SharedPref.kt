package uz.perfectalgorithm.projects.tezkor.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    var language: Boolean
        set(value) = mySharedPref.edit().putBoolean("device_token", value).apply()
        get() = mySharedPref.getBoolean("device_token",false)

    private var mySharedPref: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

}