package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea


import com.google.gson.annotations.SerializedName

data class GeneralQuickIdeaData(
    @SerializedName("average_rating")
    val averageRating: Int,
    @SerializedName("comments_count")
    val commentsCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("creator_image")
    val creatorImage: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("end_time")
    val endTime: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("idea_price")
    val ideaPrice: Int,
    @SerializedName("ratings_count")
    val ratingsCount: Int,
    @SerializedName("title")
    val title: String
)