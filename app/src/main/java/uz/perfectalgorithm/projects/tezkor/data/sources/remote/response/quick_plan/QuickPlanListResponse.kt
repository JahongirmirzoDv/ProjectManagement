package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/21/21 4:38 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.quick_plan
 **/
data class QuickPlanListResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String?,
    @field:SerializedName("previous")
    val prev: String?,
    @field:SerializedName("results")
    val quickPlans: List<QuickPlan>
)
