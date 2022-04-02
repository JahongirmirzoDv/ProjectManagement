package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.users

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse

sealed class ReceiverDataResponse {
    data class Result(

        @field:SerializedName("data")
        val data: AllWorkersResponse.DataItem? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

}