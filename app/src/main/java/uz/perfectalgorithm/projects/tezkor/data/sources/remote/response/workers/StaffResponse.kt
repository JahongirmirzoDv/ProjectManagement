package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 11/1/21 10:47 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers
 **/
data class StaffResponse(
    @field:SerializedName("data")
    val data: AllWorkersResponse.DataItem,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)