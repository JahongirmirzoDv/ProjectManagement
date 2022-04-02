package uz.perfectalgorithm.projects.tezkor.data.sources.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListToJsonConverters {
    private val gson = Gson()
    private val stringType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun jsonToStringList(value: String): ArrayList<String> {
        val newValue = if (value.isNotEmpty() && !value.startsWith("[")) {
            "[$value]"
        } else {
            value
        }
        return try {
            gson.fromJson(newValue, stringType)
        } catch (e: Exception) {
            ArrayList()
        }
    }

    @TypeConverter
    fun stringListToJson(list: ArrayList<String>) = gson.toJson(list)
}