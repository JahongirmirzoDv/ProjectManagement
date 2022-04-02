package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note

import com.google.gson.annotations.SerializedName

data class NoteDeleteOnesRequest(
    @SerializedName("note")
    private val note: Int,
    @SerializedName("exception")
    private val exception: String
)