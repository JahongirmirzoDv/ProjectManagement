package uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status

import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData

/**
 *Created by farrukh_kh on 7/27/21 12:30 PM
 *uz.rdo.projects.projectmanagement.domain.home.task.project_status
 **/
interface ProjectStatusRepository {

    suspend fun updateStatus(
        statusId: Int,
        createStatusRequest: CreateStatusRequest
    ): DataWrapper<Any>

    suspend fun deleteStatus(statusId: Int): DataWrapper<Any>

    suspend fun getStatusList(): DataWrapper<List<StatusData>>

    suspend fun createStatus(createStatusRequest: CreateStatusRequest): DataWrapper<Any>
}