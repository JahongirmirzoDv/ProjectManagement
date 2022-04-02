package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal

import com.google.gson.annotations.SerializedName

sealed class DeleteChatRequest {

    data class RequestData(
        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(
        @field:SerializedName("chat_item")
        val chatItem: Int? = null
    )
}