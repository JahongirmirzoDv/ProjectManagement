package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class UserPermissionsListResponse {

    data class Result(

        @field:SerializedName("data")
        val data: List<String>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        ) : Serializable
}
