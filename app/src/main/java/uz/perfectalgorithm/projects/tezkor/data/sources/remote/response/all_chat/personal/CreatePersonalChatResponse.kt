package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal

import com.google.gson.annotations.SerializedName

sealed class CreatePersonalChatResponse {
    data class ResponseData(
        @field:SerializedName("data")
        val data: PersonalChatListResponse.PersonalChatDataItem,

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
}

