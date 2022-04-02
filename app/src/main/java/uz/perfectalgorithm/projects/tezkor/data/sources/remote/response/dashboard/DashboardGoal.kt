package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 9/22/21 10:08 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard
 **/

data class DashboardGoalsResponseWrapper(
    @SerializedName("data")
    val data: List<DashboardGoal>
)

data class DashboardGoal(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,

    @Expose
    var isSelected: Boolean = false
) {
    override fun toString() = title
}
