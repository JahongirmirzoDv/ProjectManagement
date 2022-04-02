package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName

data class GetQuickIdeasBoxesResponse(

    @field:SerializedName("data")
    val data: List<IdeasBoxData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

/*data class DataItem(

	@field:SerializedName("ideas")
	val ideas: List<IdeasItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
)*/

data class IdeasItem(

    @field:SerializedName("rating")
    val rating: Int? = null,

    @field:SerializedName("files")
    val files: List<Any?>? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
