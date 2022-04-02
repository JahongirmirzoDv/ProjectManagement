package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class OutsourceWorkersResponse {
    data class Result(
        @field:SerializedName("data")
        val data: List<AllWorkersResponse.DataItem>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    ) : Serializable
}