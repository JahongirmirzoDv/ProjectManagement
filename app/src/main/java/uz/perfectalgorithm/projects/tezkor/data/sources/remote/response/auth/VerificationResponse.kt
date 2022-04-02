package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName

class VerificationResponse {
    data class Result(
        @field:SerializedName("data")
        val data: Data,
        @field:SerializedName("success")
        val success: Boolean,
    )

    data class Data(
        @field:SerializedName("id")
        val id: Int,
        @field:SerializedName("phone_number")
        val phoneNumber: String,
    )

}