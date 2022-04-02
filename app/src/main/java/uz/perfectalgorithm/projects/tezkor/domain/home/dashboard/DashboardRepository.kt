package uz.perfectalgorithm.projects.tezkor.domain.home.dashboard

import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.*

/**
 *Created by farrukh_kh on 9/22/21 10:11 AM
 *uz.perfectalgorithm.projects.tezkor.domain.home.dashboard
 **/
interface DashboardRepository {
    /**
     * ushbu repo dagi barcha funksiyalar DashboardApi dagilar
     * bilan bir xil vazifa bajaradi
     */

    suspend fun getStaffBelow(): DataWrapper<List<StaffBelow>>

    suspend fun getOutsourceBelow(): DataWrapper<List<StaffBelow>>

    suspend fun getFavoritesBelow(): DataWrapper<List<StaffBelow>>

    suspend fun getStructureBelow(): DataWrapper<List<DepartmentStructureBelow>>

    suspend fun getDashboardGoals(
        staffId: Int?,
        departmentId: Int?
    ): DataWrapper<List<DashboardGoal>>

    suspend fun getStatistics(
        staffId: Int?,
        departmentId: Int?,
        goalId: Int?,
        startDate: String,
        endDate: String
    ): DataWrapper<DashboardStatistics>
}