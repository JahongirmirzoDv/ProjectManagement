package uz.perfectalgorithm.projects.tezkor.domain.home.dashboard

import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.DashboardApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard.DashboardGoalsRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dashboard.DashboardStatisticsRequest
import javax.inject.Inject

/**
 *Created by farrukh_kh on 9/22/21 10:11 AM
 *uz.perfectalgorithm.projects.tezkor.domain.home.dashboard
 **/
class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApi
) : DashboardRepository {

    override suspend fun getDashboardGoals(staffId: Int?, departmentId: Int?) = try {
        val response = api.getDashboardGoals(DashboardGoalsRequest(staffId, departmentId))
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getOutsourceBelow() = try {
        val response = api.getOutsourceBelow()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getStaffBelow() = try {
        val response = api.getStaffBelow()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getFavoritesBelow() = try {
        val response = api.getFavoritesBelow()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getStructureBelow() = try {
        val response = api.getStructureBelow()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getStatistics(
        staffId: Int?,
        departmentId: Int?,
        goalId: Int?,
        startDate: String,
        endDate: String
    ) = try {
        val response =
            api.getStatistics(
                DashboardStatisticsRequest(
                    staffId,
                    departmentId,
                    goalId,
                    startDate,
                    endDate
                )
            )
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!.data)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}