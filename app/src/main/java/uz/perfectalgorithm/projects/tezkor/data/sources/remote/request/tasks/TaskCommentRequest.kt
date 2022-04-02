package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks

import com.google.gson.annotations.SerializedName

data class TaskCommentRequest(
    @field:SerializedName("parent")
    val parent: Int? = null,

    @field:SerializedName("task")
    val task: Int? = null,

    @field:SerializedName("text")
    val text: String? = null
)
