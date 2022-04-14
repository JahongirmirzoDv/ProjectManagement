package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan

import com.google.gson.annotations.SerializedName


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