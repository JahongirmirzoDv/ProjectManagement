package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/24/21 11:14 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.task
 **/
data class CreateFolderResponse(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("error")
    val error: List<Any>,
    @field:SerializedName("data")
    val data: List<CreateFolderItem>
)

data class CreateFolderItem(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("get_tasks")
    val tasks: List<Any>
)
