package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password

import com.google.gson.annotations.SerializedName

data class VerificationNewPasswordResponse(

    @field:SerializedName("data")
    val data: List<Any?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)
