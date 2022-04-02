package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse

data class QuickIdeaResponse(

    @field:SerializedName("data")
    val data: QuickIdeaData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class QuickIdeaData(

    @field:SerializedName("last_updated")
    val lastUpdated: String? = null,

    @field:SerializedName("folder")
    val folder: Int? = null,

    @field:SerializedName("get_files")
    val getFiles: List<BaseOffersItemResponse.GetFile>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
