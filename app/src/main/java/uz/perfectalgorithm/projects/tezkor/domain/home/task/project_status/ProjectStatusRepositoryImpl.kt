package uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status

import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ProjectStatusApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/27/21 12:33 PM
 *uz.rdo.projects.projectmanagement.domain.home.task.project_status
 **/
class ProjectStatusRepositoryImpl @Inject constructor(
    private val projectStatusApi: ProjectStatusApi
) : ProjectStatusRepository {

    override suspend fun getStatusList() = try {
        val response = projectStatusApi.getStatusList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun createStatus(createStatusRequest: CreateStatusRequest) = try {
        val response = projectStatusApi.createStatus(createStatusRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateStatus(statusId: Int, createStatusRequest: CreateStatusRequest) =
        try {
            val response = projectStatusApi.updateStatus(statusId, createStatusRequest)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body() ?: Any())
            } else {
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            DataWrapper.Error(e)
        }

    override suspend fun deleteStatus(statusId: Int) = try {
        val response = projectStatusApi.deleteStatus(statusId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}