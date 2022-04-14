package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan

import com.google.gson.annotations.SerializedName


data class QuickPlan(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("is_done")
    val isDone: Boolean,
    @field:SerializedName("task_status")
    val isTask: Boolean,
    @field:SerializedName("children")
    val children: List<QuickPlan>,
    @field:SerializedName("position")
    val position: Int,
)

data class QuickPlanDay(
    val weekday: String,
    val date: String,
    val quickPlans: List<QuickPlan>
)