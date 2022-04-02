package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal

import com.google.gson.annotations.SerializedName

sealed class DeleteMessagesResponse {

    data class ResponseData(
        @field:SerializedName("data")
        val data: List<Int>?,

        @field:SerializedName("is_success")
        val isSuccess: Boolean? = null,

        @field:SerializedName("message")
        val message: Message? = null,

        @field:SerializedName("action")
        val action: String? = null
    )

    data class Message(

        @field:SerializedName("ui")
        val ui: String? = null,

        @field:SerializedName("dev")
        val dev: String? = null
    )

}