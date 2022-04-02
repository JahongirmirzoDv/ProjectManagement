package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import java.io.Serializable

data class GetRatingIdeaResponse(

    @field:SerializedName("data")
    val data: RatingIdea? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class Creator(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null
) : Serializable

data class RatingIdea(

    @field:SerializedName("creator")
    val creator: Creator? = null,

    @field:SerializedName("is_rated")
    val isRated: Boolean? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("files")
    val files: List<BaseOffersItemResponse.GetFile>? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("folder")
    val folder: Int? = null
) : Serializable
