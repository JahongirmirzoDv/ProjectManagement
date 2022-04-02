package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName

data class DeleteQuickIdeaResponse(

    @field:SerializedName("data")
    val data: QuickIdeaData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

