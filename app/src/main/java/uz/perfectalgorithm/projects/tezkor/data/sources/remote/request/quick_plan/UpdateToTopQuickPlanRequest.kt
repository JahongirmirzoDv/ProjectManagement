package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.quick_plan

import com.google.gson.annotations.SerializedName

data class UpdateToTopQuickPlanRequest(
    @field:SerializedName("parent")
    val parentId: String? = null,

    @field:SerializedName("position")
    val position: Position? = null,
    )


data class Position(
    @field:SerializedName("upper")
    val upper: Int? = null,

    @field:SerializedName("lower")
    val lower: Int? = null,
    )