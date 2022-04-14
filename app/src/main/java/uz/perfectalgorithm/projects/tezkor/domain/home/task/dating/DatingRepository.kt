package uz.perfectalgorithm.projects.tezkor.domain.home.task.dating

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.CreateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.UpdateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dating.DatingListItem



interface DatingRepository {

    suspend fun createDating(request: CreateDatingRequest): DataWrapper<Any>

    fun getDatingList(): Flow<Result<List<DatingListItem>>>

    suspend fun getDatingListNew(): DataWrapper<List<DatingListItem>>

    suspend fun getDatingById(datingId: Int): DataWrapper<DatingDetails>

    suspend fun deleteDating(datingId: Int): DataWrapper<Any>

    suspend fun updateDating(
        datingId: Int,
        updateDatingRequest: UpdateDatingRequest
    ): DataWrapper<Any>
}