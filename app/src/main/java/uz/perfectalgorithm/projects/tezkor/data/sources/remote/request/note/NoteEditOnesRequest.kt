package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note

import com.google.gson.annotations.SerializedName

data class NoteEditOnesRequest(
    @SerializedName("note")
    private val note: Int,
    @SerializedName("exception")
    private val exception: String,
    @SerializedName("time")
    private val startTime: String
)