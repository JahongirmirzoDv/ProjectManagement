package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal

import com.google.gson.annotations.SerializedName

data class UpdateTaskWithChatRequest(
    @field:SerializedName("is_done")
    val isDone: Boolean,
)