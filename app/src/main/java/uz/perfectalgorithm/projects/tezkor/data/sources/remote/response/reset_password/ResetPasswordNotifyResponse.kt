package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password

import com.google.gson.annotations.SerializedName


sealed class ResetPasswordResponse {

    data class ResetPasswordNotifyResponse(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: Message? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class Dev(

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: String? = null
    )

    data class Message(

        @field:SerializedName("ui")
        val ui: String? = null,

        @field:SerializedName("dev")
        val dev: Dev? = null
    )

    data class Data(
        val any: Any? = null
    )

}

