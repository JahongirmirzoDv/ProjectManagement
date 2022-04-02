package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea

import com.google.gson.annotations.SerializedName

data class CategoryIdeaByFinishedResponse(

    @field:SerializedName("ideas")
    val ideas: List<FinishedIdeaItem>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class FinishedIdeaItem(

    @field:SerializedName("comments_count")
    val commentsCount: Int? = null,

    @field:SerializedName("end_time")
    val endTime: String? = null,

    @field:SerializedName("idea_price")
    val ideaPrice: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("average_rating")
    val averageRating: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("ratings_count")
    val ratingsCount: Int? = null,

    @field:SerializedName("creator_image")
    val creatorImage: String? = null
)
