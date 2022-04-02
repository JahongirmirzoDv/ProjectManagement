package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea

data class GetQuickIdeaResponse(

    @field:SerializedName("data")
    val data: RatingIdea? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

