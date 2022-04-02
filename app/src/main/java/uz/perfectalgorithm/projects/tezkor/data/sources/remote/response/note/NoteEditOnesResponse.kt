package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NoteEditOnesResponse(
    @SerializedName("data")
    val data: NoteEditOnesData,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: List<Any>
)

data class NoteEditOnesData(
    @SerializedName("exception")
    val exception: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("note")
    val title: Int,
)