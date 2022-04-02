package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName

data class PostTaskCommentResponse(

    @field:SerializedName("parent")
    val parent: Int? = null,

    @field:SerializedName("task")
    val task: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null
)
