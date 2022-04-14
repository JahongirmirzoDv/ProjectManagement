package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan

import com.google.gson.annotations.SerializedName


data class CreateTacticPlanResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("status")
    val status: Int,
    @field:SerializedName("date")
    val date: String
)