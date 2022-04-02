package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotePostResponse(
    @SerializedName("data")
    val data: NoteData,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: List<Any>
)

data class NoteData(
    @SerializedName("time")
    val startTime: String,
    @SerializedName("repeat")
    val repeat: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("until_date")
    val untilDate: String?,
    @SerializedName("reminders")
    val reminder: MutableList<Int> = mutableListOf(),
    @SerializedName("repeat_rule")
    val repeatRule: Int,
    @SerializedName("repeat_exceptions")
    val repeatExceptions: ArrayList<String> = ArrayList()
) : Serializable