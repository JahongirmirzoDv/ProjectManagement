package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard

import com.google.gson.annotations.SerializedName
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse

/**
 *Created by farrukh_kh on 9/22/21 9:53 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard
 **/
data class StatisticsResponseWrapper(
    @SerializedName("data")
    val data: DashboardStatistics
)

data class DashboardStatistics(
    @SerializedName("meetings_count")
    val meetingsCount: Any?,
    @SerializedName("datings_count")
    val datingsCount: Any?,
    @SerializedName("quick_plans_count")
    val quickPlansCount: Int,
    @SerializedName("goal_percent")
    val goalPercent: Float?,
    @SerializedName("goals")
    val goals: DashboardDataItem,
    @SerializedName("projects")
    val projects: DashboardDataItem,
    @SerializedName("tasks")
    val tasks: DashboardDataItem,
    @SerializedName("offers")
    val offers: DashboardDataItem,
    @SerializedName("complaints")
    val complaints: DashboardDataItem,
    @SerializedName("staffs_by_department")
    val staffsByDepartment: List<StaffSByDepartmentItem>,
    @SerializedName("all_staff")
    val allStaff: List<AllWorkersResponse.DataItem>
)

data class DashboardDataItem(
    @SerializedName("count")
    val count: Int,
    @SerializedName("by_status")
    val byStatus: List<ByStatusItem>,
)

data class ByStatusItem(
    @SerializedName("status_id")
    val statusId: Int? = null,
    @SerializedName("status_title")
    val statusTitle: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("items_count")
    val itemCount: Int,
)

data class StaffSByDepartmentItem(
    @SerializedName("department_id")
    val departmentId: Int,
    @SerializedName("department_title")
    val departmentTitle: String,
    @SerializedName("staffs_count")
    val staffsCount: Int,
)