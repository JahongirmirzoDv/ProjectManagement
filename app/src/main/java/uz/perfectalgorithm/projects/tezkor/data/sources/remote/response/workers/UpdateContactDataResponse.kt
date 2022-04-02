package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class UpdateContactDataResponse {

    data class Result(
        @field:SerializedName("data")
        val data: Data,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable

    data class Data(

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("user")
        val workerId: Int,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,
    ) : Serializable


}
