package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment

import com.google.gson.annotations.SerializedName

data class PostCommentResponse(

    @field:SerializedName("data")
    val data: CommentData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class CommentData(

    @field:SerializedName("reply_to")
    val replyTo: Any? = null,

    @field:SerializedName("idea")
    val idea: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("user")
    val user: User? = null
)

data class User(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null
)
