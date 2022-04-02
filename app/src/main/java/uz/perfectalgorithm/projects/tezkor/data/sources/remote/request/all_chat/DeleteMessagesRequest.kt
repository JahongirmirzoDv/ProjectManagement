package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat

import com.google.gson.annotations.SerializedName

sealed class DeleteMessagesRequest {
    data class RequestData(
        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(
        @field:SerializedName("message_ids")
        val messageIds: List<Int>? = null
    )

}