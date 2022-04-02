package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile

import com.google.gson.annotations.SerializedName

sealed class UpdateUserDataResponse {
    data class Result(

        @field:SerializedName("data")
        val data: Data? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null
    )

    data class Data(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("birth_date")
        val birthDate: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null
    )
}