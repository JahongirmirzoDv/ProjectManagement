package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName

sealed class CreateWorkerResponse {

    data class Result(
        @field:SerializedName("data")
        val data: DataItem? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class DataItem(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("birth_date")
        val birthDate: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("company")
        val company: Int? = null,

        @field:SerializedName("phone_number")
        val phoneNumber: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("position")
        val position: List<Int?>? = null,

        @field:SerializedName("department")
        val department: Int? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("email")
        val email: String? = null
    )

}
