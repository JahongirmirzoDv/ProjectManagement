package uz.perfectalgorithm.projects.tezkor.domain.home.others

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import java.io.File

interface OthersRepository {
    fun getUserData(): Flow<Result<UserDataResponse.Data>>
    suspend fun getUserDataWrapper(): DataWrapper<UserDataResponse.Data>
    fun logoutUser(): Flow<Result<Boolean>>
    fun updateUserData(
        updateUserDataRequest: UpdateUserDataRequest,
        image: File?
    ): Flow<Result<UpdateUserDataResponse.Data>>
}