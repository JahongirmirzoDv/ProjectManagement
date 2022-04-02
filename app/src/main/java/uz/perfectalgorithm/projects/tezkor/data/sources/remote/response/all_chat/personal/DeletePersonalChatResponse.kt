package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal

import com.google.gson.annotations.SerializedName

sealed class DeletePersonalChatResponse {
    data class ResponseData(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("action")
        val action: String? = null,

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

    data class Data(

        @field:SerializedName("deleted_chat_id")
        val deletedChatId: Int? = null
    )
}
