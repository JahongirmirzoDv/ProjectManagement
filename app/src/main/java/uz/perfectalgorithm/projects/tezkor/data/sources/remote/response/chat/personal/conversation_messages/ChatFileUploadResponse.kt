package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.personal.conversation_messages

import com.google.gson.annotations.SerializedName

sealed class ChatFileUploadResponse {

    data class Result(

        @field:SerializedName("data")
        val data: FileUploadData? = null,

        @field:SerializedName("is_success")
        val isSuccess: Boolean? = null,

        @field:SerializedName("message")
        val message: Message? = null
    )

    data class Message(

        @field:SerializedName("ui")
        val ui: String? = null,

        @field:SerializedName("dev")
        val dev: String? = null
    )

    data class FileUploadData(

        @field:SerializedName("file")
        val file: String? = null,

        @field:SerializedName("time_created")
        val timeCreated: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("chat_message")
        val chatMessage: Any? = null,

        @field:SerializedName("time_updated")
        val timeUpdated: String? = null
    )
}