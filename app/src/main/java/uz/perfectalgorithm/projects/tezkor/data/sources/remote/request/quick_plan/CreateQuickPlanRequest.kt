package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/18/21 11:46 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.quick_plan
 **/
data class CreateQuickPlanRequest(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("parent")
    val parentId: Int? = null,
)