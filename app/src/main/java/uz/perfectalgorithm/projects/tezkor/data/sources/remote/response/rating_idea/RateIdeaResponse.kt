package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea

import com.google.gson.annotations.SerializedName

data class RateIdeaResponse(

    @field:SerializedName("data")
    val data: RatingDataBody? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class RatingDataBody(

    @field:SerializedName("rate")
    val rate: Int? = null
)
