package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat

import com.google.gson.annotations.SerializedName

sealed class DeleteMessagesGroupRequest {
    data class RequestData(
        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(
        @field:SerializedName("messages_list")
        val messageIds: List<Int>? = null
    )

}