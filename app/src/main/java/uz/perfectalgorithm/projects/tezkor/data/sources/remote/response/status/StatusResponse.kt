package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status

import com.google.gson.annotations.SerializedName

data class StatusResponse(

    @field:SerializedName("data")
    val data: List<StatusData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class StatusData(

    @field:SerializedName("company")
    val company: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
