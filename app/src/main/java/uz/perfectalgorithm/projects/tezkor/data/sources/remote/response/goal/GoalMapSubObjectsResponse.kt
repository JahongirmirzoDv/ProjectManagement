package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal

import com.google.gson.annotations.SerializedName

data class GoalMapSubObjectsResponse(

    @field:SerializedName("data")
    val data: ItemGoalMapData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class TasksItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("tasks_count")
    val tasksCount: Int? = null,

    @field:SerializedName("tasks")
    val tasks: List<TasksItem>? = null,

    @field:SerializedName("performed_percent")
    val performedPercent: Int? = null
)

data class GoalsItem(

    @field:SerializedName("projects")
    val projects: List<ProjectsItem?>? = null,

    @field:SerializedName("projects_count")
    val projectsCount: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("performed_percent")
    val performedPercent: Double? = null
)

data class ProjectsItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("tasks_count")
    val tasksCount: Int? = null,

    @field:SerializedName("tasks")
    val tasks: List<TasksItem?>? = null,

    @field:SerializedName("performed_percent")
    val performedPercent: Double? = null
)

data class ItemGoalMapData(

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("performed_percent")
    val performedPercent: Double? = null,

    @field:SerializedName("goals")
    val goals: List<GoalsItem>
)
