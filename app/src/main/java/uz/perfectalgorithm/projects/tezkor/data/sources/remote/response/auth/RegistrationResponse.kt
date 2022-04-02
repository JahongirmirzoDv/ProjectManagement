package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName

class RegistrationResponse {

    data class Result(
        @field:SerializedName("success")
        val success: Boolean,
        @field:SerializedName("message")
        val message: Message
    )

    data class Dev(
        @field:SerializedName("message")
        val message: String,
        @field:SerializedName("status")
        val status: String
    )

    data class Message(
        @field:SerializedName("dev")
        val dev: Dev,
        @field:SerializedName("ui")
        val ui: String
    )

    data class ErrorModel(
        @field:SerializedName("message")
        val message: String,
        @field:SerializedName("error_code")
        val errorCode: Int
    )


}

