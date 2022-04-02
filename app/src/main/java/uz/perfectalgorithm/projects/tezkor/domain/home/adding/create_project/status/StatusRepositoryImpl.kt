package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ProjectStatusApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.TaskStatusApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 10.07.2021 11:14
 **/
class StatusRepositoryImpl @Inject constructor(
    private val taskStatusApi: TaskStatusApi,
    private val projectStatusApi: ProjectStatusApi,
) : StatusRepository {

    override fun getTaskStatusList(): Flow<Result<List<StatusData>>> = flow {
        try {
            val response = taskStatusApi.getStatusList()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<List<StatusData>>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getProjectStatusList(): Flow<Result<List<StatusData>>> = flow {
        try {
            val response = projectStatusApi.getStatusList()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<List<StatusData>>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }

    }
}