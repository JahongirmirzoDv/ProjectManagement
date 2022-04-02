package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/18/21 11:52 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.quick_plan
 **/
data class QuickPlanCreateResponse(
    @field:SerializedName("data")
    val data: QuickPlan
)