package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.chat.personal.conversation_messages

import com.google.gson.annotations.SerializedName

sealed class SendMessageRequest {
    data class RequestData(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Data(

        @field:SerializedName("message_type")
        val messageType: String? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("answer_for")
        val answerFor: Int? = null,

        @field:SerializedName("files")
        val files: List<Int>? = null,

        )
}