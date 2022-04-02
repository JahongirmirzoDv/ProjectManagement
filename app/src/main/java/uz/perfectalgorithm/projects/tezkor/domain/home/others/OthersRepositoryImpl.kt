package uz.perfectalgorithm.projects.tezkor.domain.home.others

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.OthersApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import java.io.File
import javax.inject.Inject

class OthersRepositoryImpl @Inject constructor(
    private val api: OthersApi,
    private val storage: LocalStorage
) : OthersRepository {

    override fun getUserData(): Flow<Result<UserDataResponse.Data>> = flow {
        try {
            val response = api.getUserData(storage.userId)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<UserDataResponse.Data>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else emit(
                Result.failure<UserDataResponse.Data>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<UserDataResponse.Data>(e))
        }
    }

    override suspend fun getUserDataWrapper() = try {
        val response = api.getUserData(storage.userId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun logoutUser(): Flow<Result<Boolean>> = flow {
        try {
            val response = api.logout()
            if (response.code() == 204) {
                emit(Result.success(true))
            } else emit(
                Result.failure<Boolean>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<Boolean>(e))
        }
    }

    override fun updateUserData(
        updateUserDataRequest: UpdateUserDataRequest,
        image: File?
    ): Flow<Result<UpdateUserDataResponse.Data>> =
        flow {
            try {
                if (image == null) {
                    val response = api.updateUserData(updateUserDataRequest)
                    if (response.code() == 200) response.body()?.let {
                        if (it.data != null) {
                            emit(Result.success(it.data))
                        } else {
                            emit(
                                Result.failure<UpdateUserDataResponse.Data>(
                                    Exception(
                                        App.instance.resources.getString(
                                            R.string.error
                                        )
                                    )
                                )
                            )
                        }
                    } else {
                        Result.failure<UpdateUserDataResponse.Data>(HttpException(response))
                    }

                } else {
                    val filePart = MultipartBody.Part.createFormData(
                        "image", "image.jpg", RequestBody.create(
                            "image/JPEG".toMediaTypeOrNull(),
                            image.readBytes()
                        )
                    )
                    val response = api.updateUserDataWithImage(
                        firstName = updateUserDataRequest.firstName!!.toRequestBody("text/plain".toMediaTypeOrNull()),
                        lastName = updateUserDataRequest.lastName!!.toRequestBody("text/plain".toMediaTypeOrNull()),
                        birthDate = updateUserDataRequest.birthDate!!.toRequestBody("text/plain".toMediaTypeOrNull()),
                        part = filePart
                    )
                    if (response.code() == 200) response.body()?.let {
                        if (it.data != null) {
                            emit(Result.success(it.data))
                        } else {
                            emit(
                                Result.failure<UpdateUserDataResponse.Data>(
                                    Exception(
                                        App.instance.resources.getString(
                                            R.string.error
                                        )
                                    )
                                )
                            )
                        }
                    } else {
                        emit(Result.failure<UpdateUserDataResponse.Data>(HttpException(response)))
                    }
                }
            } catch (e: Exception) {
                emit(Result.failure<UpdateUserDataResponse.Data>(e))
            }
        }
}