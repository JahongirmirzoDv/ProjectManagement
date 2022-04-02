package uz.perfectalgorithm.projects.tezkor.data.sources.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListToIntegerConverters {
    private val gson = Gson()
    private val stringType = object : TypeToken<List<Int>>() {}.type

    @TypeConverter
    fun jsonToIntList(value: String): ArrayList<Int> {
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
    fun integerListToJson(list: ArrayList<Int>) = gson.toJson(list)
}