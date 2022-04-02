package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 7/31/21 12:22 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.tactic_plan
 **/
data class UpdateTacticPlanResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("status")
    val statusId: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String
)