package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName

data class CreateQuickIdeaBoxResponse(

    @field:SerializedName("data")
    val data: IdeasBoxData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class IdeasBoxData(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("count_ideas")
    var countIdeas: Int? = null,

    @field:SerializedName("title")
    var title: String? = null
)
