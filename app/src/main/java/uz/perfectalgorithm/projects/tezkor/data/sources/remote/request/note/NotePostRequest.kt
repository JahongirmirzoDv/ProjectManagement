package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note

import com.google.gson.annotations.SerializedName

data class NotePostRequest(
    @SerializedName("time")
    private val time: String,
    @SerializedName("message_id")
    private var message_id: Int? = null,
    @SerializedName("repeat")
    private val repeat: String,
    @SerializedName("description")
    private val description: String,
    @SerializedName("title")
    private val title: String,
    @SerializedName("repeat_rule")
    private val repeatRule: Int = 0,
    @SerializedName("new_reminders")
    private val reminders: MutableList<Int> = mutableListOf(),
    @SerializedName("until_date")
    private val untilDate: String? = ""
)