package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class AllWorkersShortDataResponse {
    data class Result(

        @field:SerializedName("data")
        val data: List<WorkerShortDataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable

    data class WorkerShortDataItem(
        @field: SerializedName("full_name")
        var fullName: String?,

        @field: SerializedName("id")
        val id: Int?,

        @field: SerializedName("user")
        val userId: Int?,

        @field: SerializedName("image")
        val image: String?,

        @field:SerializedName("role")
        val role: String? = null,

        @Expose
        var isChecked: Boolean = false

    ) : Serializable


}
