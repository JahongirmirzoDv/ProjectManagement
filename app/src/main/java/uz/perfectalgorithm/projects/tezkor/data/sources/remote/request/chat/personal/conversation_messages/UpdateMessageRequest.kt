package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal.conversation_messages

import com.google.gson.annotations.SerializedName

sealed class UpdateMessageRequest {
    data class RequestData(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(

        @field:SerializedName("message_id")
        val messageId: Int,

        @field:SerializedName("new_message")
        val newMessage: String? = null,

        )
}