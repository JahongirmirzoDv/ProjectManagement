package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/19/21 8:18 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.quick_plan
 **/
data class UpdateQuickPlanRequest(
    @field:SerializedName("title")
    val title: String? = null,
    @field:SerializedName("date")
    val date: String? = null,
    @field:SerializedName("parent")
    val parentId: Int? = null,
    @field:SerializedName("is_done")
    val isDone: Boolean? = null,
)
