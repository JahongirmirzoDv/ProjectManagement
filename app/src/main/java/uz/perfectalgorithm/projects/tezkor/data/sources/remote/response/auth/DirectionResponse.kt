package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName

class DirectionResponse {

    data class Result(
        @field:SerializedName("data")
        val data: List<Direction>,

        @field:SerializedName("success")
        val success: Boolean,

        @field:SerializedName("message")
        val message: String,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class Direction(
        @field:SerializedName("id")
        val id: Int,
        @field:SerializedName("title")
        val title: String
    )
}