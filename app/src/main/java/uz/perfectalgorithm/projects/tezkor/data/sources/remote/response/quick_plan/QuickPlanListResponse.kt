package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan

import com.google.gson.annotations.SerializedName


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
