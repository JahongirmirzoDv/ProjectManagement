package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/26/21 9:21 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.task
 **/
data class CreateStatusResponse(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("error")
    val error: List<Any>,
    @field:SerializedName("data")
    val data: CreateStatusItem
)

data class CreateStatusItem(
    @field:SerializedName("id")
    val id: Int?,
    @field:SerializedName("title")
    val title: String
)
