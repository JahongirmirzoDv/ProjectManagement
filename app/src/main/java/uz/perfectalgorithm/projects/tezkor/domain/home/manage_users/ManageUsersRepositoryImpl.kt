package uz.perfectalgorithm.projects.tezkor.domain.home.manage_users

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ManageUsersApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsers
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

class ManageUsersRepositoryImpl @Inject constructor(private val managerUsersApi: ManageUsersApi) :
    ManageUsersRepository {
    override fun getManageUsers(): Flow<Result<List<ManageUsers>>> = flow {
        try {
            val response = managerUsersApi.getManageUsers()
            timberLog(response.message())
            if (response.isSuccessful) {
                response.body()?.let { emit(Result.success(it.users)) }
            } else {
                emit(
                    Result.failure<List<ManageUsers>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<List<ManageUsers>>(e))
        }
    }
}