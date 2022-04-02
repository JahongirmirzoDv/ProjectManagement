package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment

import com.google.gson.annotations.SerializedName

data class GetCommentListResponse(

    @field:SerializedName("data")
    val data: CommentList? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)


data class CommentList(

    @field:SerializedName("pages_count")
    val pagesCount: Int? = null,

    @field:SerializedName("all_comments_count")
    val allCommentsCount: Int? = null,

    @field:SerializedName("comments_list")
    val commentsList: List<CommentData>? = null
)

