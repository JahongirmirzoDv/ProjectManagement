package uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status

import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.TaskStatusApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/26/21 6:18 PM
 *uz.rdo.projects.projectmanagement.domain.home.task.task_status
 **/
class TaskStatusRepositoryImpl @Inject constructor(
    private val taskStatusApi: TaskStatusApi
) : TaskStatusRepository {

    override suspend fun getStatusList() = try {
        val response = taskStatusApi.getStatusList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }


    override suspend fun createStatus(createStatusRequest: CreateStatusRequest) = try {
        val response = taskStatusApi.createStatus(createStatusRequest)
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
            val response = taskStatusApi.updateStatus(statusId, createStatusRequest)
            if (response.isSuccessful) {
                DataWrapper.Success(response.body() ?: Any())
            } else {
                DataWrapper.Error(HttpException(response))
            }
        } catch (e: Exception) {
            DataWrapper.Error(e)
        }

    override suspend fun deleteStatus(statusId: Int) = try {
        val response = taskStatusApi.deleteStatus(statusId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}