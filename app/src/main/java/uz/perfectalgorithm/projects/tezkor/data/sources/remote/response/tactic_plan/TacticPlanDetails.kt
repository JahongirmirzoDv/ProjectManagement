package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 8/23/21 9:49 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.response.tactic_plan
 **/
data class TacticPlanDetails(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("status")
    val status: Status,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("description")
    val description: String
)