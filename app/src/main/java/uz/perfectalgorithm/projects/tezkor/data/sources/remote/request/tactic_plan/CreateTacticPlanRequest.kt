package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tactic_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/29/21 10:47 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.tactic_plan
 **/
data class CreateTacticPlanRequest(
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("status")
    val statusId: Int,
    @field:SerializedName("title")
    val title: String
)