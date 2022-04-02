package uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData

/**
 * Created by Jasurbek Kurganbaev on 14.07.2021 16:52
 **/
interface RepetitionRepository {

    suspend fun getRepetitionsNew(): DataWrapper<List<RepetitionData>>

    fun getRepetitions(): Flow<Result<List<RepetitionData>>>
}