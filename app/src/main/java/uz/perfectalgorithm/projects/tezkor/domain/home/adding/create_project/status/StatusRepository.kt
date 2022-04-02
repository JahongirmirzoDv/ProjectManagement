package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData

/**
 * Created by Jasurbek Kurganbaev on 10.07.2021 11:14
 **/
interface StatusRepository {

    fun getTaskStatusList(): Flow<Result<List<StatusData>>>

    fun getProjectStatusList(): Flow<Result<List<StatusData>>>
}