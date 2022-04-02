package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.all_chat

import com.google.gson.annotations.SerializedName

sealed class SendChatMessageRequest {

    data class RequestData(
        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(
        @field:SerializedName("chat_id")
        val chatId: Int,
        @field:SerializedName("message")
        val message: String,
        @field:SerializedName("message_type")
        val messageType: String,

        @field:SerializedName("answer_for")
        val answerFor: Int? = null,
        @field:SerializedName("files")
        val files: List<Int>? = null
    )
}
