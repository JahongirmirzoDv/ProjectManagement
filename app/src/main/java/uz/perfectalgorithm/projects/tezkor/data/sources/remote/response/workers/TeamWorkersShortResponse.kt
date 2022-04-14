package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName


data class TeamWorkersShortResponse(
    @field:SerializedName("data")
    val data: List<AllWorkersShort>,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)