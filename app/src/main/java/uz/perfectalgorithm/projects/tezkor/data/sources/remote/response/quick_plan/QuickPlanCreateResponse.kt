package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan

import com.google.gson.annotations.SerializedName


data class QuickPlanCreateResponse(
    @field:SerializedName("data")
    val data: QuickPlan
)