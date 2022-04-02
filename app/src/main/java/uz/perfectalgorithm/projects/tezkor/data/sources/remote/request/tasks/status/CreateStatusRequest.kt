package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/26/21 9:19 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.tasks.status
 **/
data class CreateStatusRequest(
    @field:SerializedName("company")
    val companyId: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("is_viewable")
    val isViewable: Boolean
)
