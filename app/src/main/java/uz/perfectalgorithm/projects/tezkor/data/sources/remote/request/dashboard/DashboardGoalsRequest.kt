package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard

import com.google.gson.annotations.SerializedName

/**
 *Created by farrukh_kh on 9/30/21 3:09 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard
 **/
data class DashboardGoalsRequest(
    @SerializedName("user_id")
    val staffId: Int?,
    @SerializedName("department_id")
    val departmentId: Int?
)