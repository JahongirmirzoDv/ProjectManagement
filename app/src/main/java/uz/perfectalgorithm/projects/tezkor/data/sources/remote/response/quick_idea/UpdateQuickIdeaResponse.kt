package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName

data class UpdateQuickIdeaResponse(

    @field:SerializedName("data")
    val data: UpdateIdeaItem? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class UpdateIdeaItem(

    @field:SerializedName("start_time")
    val startTime: String? = null,

    @field:SerializedName("is_done")
    val isDone: Boolean? = null,

    @field:SerializedName("to_idea_box")
    val toIdeaBox: Boolean? = null,

    @field:SerializedName("folder")
    val folder: Int? = null,

    @field:SerializedName("idea_price")
    val ideaPrice: Int? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
