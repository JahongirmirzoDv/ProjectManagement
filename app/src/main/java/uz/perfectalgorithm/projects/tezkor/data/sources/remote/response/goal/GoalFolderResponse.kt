package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal

import com.google.gson.annotations.SerializedName

data class GoalFolderResponse(

    @field:SerializedName("data")
    val data: ItemGoalMapData?,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class GoalFolderListResponse(

    @field:SerializedName("data")
    val data: List<ItemGoalMapData>?,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class GoalMapData(

    @field:SerializedName("get_goals")
    val getGoals: List<GoalData?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)

