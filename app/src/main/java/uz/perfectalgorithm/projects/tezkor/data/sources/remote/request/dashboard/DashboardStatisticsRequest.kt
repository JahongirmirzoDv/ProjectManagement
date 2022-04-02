package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 9/30/21 3:12 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard
 **/
data class DashboardStatisticsRequest(
    @SerializedName("user_id")
    val staffId: Int?,
    @SerializedName("department_id")
    val departmentId: Int?,
    @SerializedName("goal_id")
    val goalId: Int?,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String
)