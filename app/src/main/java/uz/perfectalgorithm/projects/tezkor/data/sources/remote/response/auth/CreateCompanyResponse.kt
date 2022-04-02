package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName

class CreateCompanyResponse {
    data class Result(
        @field:SerializedName("data")
        val data: CreateCompanyData,

        @field:SerializedName("success")
        val success: Boolean,

        @field:SerializedName("message")
        val message: String
    )

    data class CreateCompanyData(
        @field:SerializedName("direction")
        val direction: String,
        @field:SerializedName("email")
        val email: String,
        @field:SerializedName("id")
        val id: Int,
        @field:SerializedName("image")
        val image: Any,
        @field:SerializedName("title")
        val title: String,
        @field:SerializedName("username")
        val username: String
    )
}