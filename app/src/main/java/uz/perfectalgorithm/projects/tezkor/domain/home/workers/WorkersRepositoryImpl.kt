package uz.perfectalgorithm.projects.tezkor.domain.home.workers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.WorkersApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons.AllPositionsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreatePositionResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructurePositionsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import java.io.File
import javax.inject.Inject

/**
 * Created by Davronbek Daximjanov on 17.06.2021
 **/

class WorkersRepositoryImpl @Inject constructor(
    private val api: WorkersApi,
) : WorkersRepository {

    override suspend fun getOutsourceWorkersNew() = try {
        val response = api.getOutsourceList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getStaff(staffId: Int) = try {
        val response = api.getStaff(staffId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getUserData(): Flow<Result<UserDataResponse.Data>> = flow {
        try {
            val response = api.getUserData()
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
            timberLog("User Data get Error = $e")
            emit(Result.failure<UserDataResponse.Data>(e))
        }
    }

    override fun getTeamWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>> = flow {
        try {
            val response = api.getTeamWorkers()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersResponse.DataItem>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get Team Workers error = $e")
            emit(Result.failure<List<AllWorkersResponse.DataItem>>(e))
        }
    }

    override fun getTeamWorkersShortFlow(): Flow<Result<List<AllWorkersShort>>> = flow {
        try {
            val response = api.getTeamWorkersShort()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersShort>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get Team Workers error = $e")
            emit(Result.failure<List<AllWorkersShort>>(e))
        }
    }

    override suspend fun getTeamWorkersNew() = try {
        val response = api.getTeamWorkers()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getAllWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>> = flow {
        try {
            val response = api.getAllWorkers()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersResponse.DataItem>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get All Workers error = $e")
            emit(Result.failure<List<AllWorkersResponse.DataItem>>(e))
        }
    }

    override fun getAllWorkersShortFlow(): Flow<Result<List<AllWorkersShort>>> = flow {
        try {
            val response = api.getAllWorkersShort()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersShort>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get All Workers error = $e")
            emit(Result.failure<List<AllWorkersShort>>(e))
        }
    }

    override suspend fun getAllWorkersNew() = try {
        val response = api.getAllWorkers()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getStructure(): Flow<Result<List<StructureResponse.DataItem>>> = flow {
        try {
            val response = api.getStructure()
            if (response.code() == 200) response.body().let {
                emit(Result.success(it!!.data!!.structureList) as Result<List<StructureResponse.DataItem>>)
            } else {
                Result.failure<List<StructureResponse.DataItem>>(
                    HttpException(response)
                )
            }
        } catch (e: Exception) {
            timberLog("Get Structure error = $e")
            emit(Result.failure<List<StructureResponse.DataItem>>(e))
        }
    }

    override fun getStructureShortFlow(): Flow<Result<List<DepartmentStructureShort>>> = flow {
        try {
            val response = api.getStructureShort()
            if (response.code() == 200) response.body().let {
                emit(Result.success<List<DepartmentStructureShort>>(it!!.data!!.structureList!!.filterNotNull()))
            } else {
                Result.failure<List<DepartmentStructureShort>>(
                    HttpException(response)
                )
            }
        } catch (e: Exception) {
            timberLog("Get Structure error = $e")
            emit(Result.failure<List<DepartmentStructureShort>>(e))
        }
    }

    override suspend fun getStructureNew() = try {
        val response = api.getStructure()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data?.structureList ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun createWorker(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        positions: List<Int>,
        gender: String,
        isOutsource: Boolean
    ): Flow<Result<CreateWorkerResponse.DataItem>> = flow {
        try {
            val response = api.createWorker(
                CreateWorkerRequest(
                    phone_number = phone,
                    position = positions,
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                    birth_date = dateOfBirth,
                    password = password,
                    sex = gender,
                    is_outsource = isOutsource
                )
            )
            if (response.code() == 201) response.body().let {
                emit(Result.success(it!!.data) as Result<CreateWorkerResponse.DataItem>)
            } else {
                emit(
                    Result.failure<CreateWorkerResponse.DataItem>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Create Worker error = $e")
            emit(Result.failure<CreateWorkerResponse.DataItem>(e))
        }
    }

    override fun createWorkerWithImage(
        name: String,
        lastName: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        password: String,
        positions: List<Int>,
        gender: String,
        file: File,
        isOutsource: Boolean
    ): Flow<Result<CreateWorkerResponse.DataItem>> = flow {
        try {
            val positionsBody = Array(positions.size) {
                positions[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val filePart = MultipartBody.Part.createFormData(
                "image", "image.jpg", RequestBody.create(
                    "image/JPEG".toMediaTypeOrNull(),
                    file.readBytes()
                )
            )

            val response = api.createWorkerWithImage(
                firstName = name.toRequestBody("text/plain".toMediaTypeOrNull()),
                lastName = lastName.toRequestBody("text/plain".toMediaTypeOrNull()),
                dateOfBirth = dateOfBirth.toRequestBody("text/plain".toMediaTypeOrNull()),
                phone = phone.toRequestBody("text/plain".toMediaTypeOrNull()),
                email = email.toRequestBody("text/plain".toMediaTypeOrNull()),
                password = password.toRequestBody("text/plain".toMediaTypeOrNull()),
                positions = positionsBody,
                gender = gender.toRequestBody("text/plain".toMediaTypeOrNull()),
                part = filePart,
                isOutsource = isOutsource.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
            )

            if (response.code() == 201) response.body().let {
                emit(Result.success(it!!.data) as Result<CreateWorkerResponse.DataItem>)
            } else {
                emit(
                    Result.failure<CreateWorkerResponse.DataItem>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Create Worker error = $e")
            emit(Result.failure<CreateWorkerResponse.DataItem>(e))
        }
    }

    override fun getStructureWithPositions(): Flow<Result<List<StructurePositionsResponse.DataItem>>> =
        flow {
            try {
                val response = api.getStructureWithPositions()
                if (response.code() == 200) response.body().let {
                    emit(Result.success(it!!.data) as Result<List<StructurePositionsResponse.DataItem>>)
                } else {
                    Result.failure<List<StructureResponse.DataItem>>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Structure error = $e")
                emit(Result.failure<List<StructurePositionsResponse.DataItem>>(e))
            }
        }


    override fun createPosition(createPositionRequest: CreatePositionRequest): Flow<Result<CreatePositionResponse.PositionData>> =
        flow {
            try {
                val response = api.createPosition(createPositionRequest)

                if (response.code() == 201) response.body().let {
                    emit(Result.success(it!!.data) as Result<CreatePositionResponse.PositionData>)
                } else {
                    Result.failure<CreatePositionResponse.PositionData>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Department error = $e")
                emit(Result.failure<CreatePositionResponse.PositionData>(e))
            }
        }

    override fun addUserToFavourites(addUserToFavouritesRequest: AddUserToFavouritesRequest): Flow<Result<WorkerDataResponse.WorkerData>> =
        flow {
            try {
                val response = api.addUserToFavourites(addUserToFavouritesRequest)

                if (response.code() == 200) response.body().let {
                    emit(Result.success(it!!.data) as Result<WorkerDataResponse.WorkerData>)
                } else {
                    Result.failure<WorkerDataResponse.WorkerData>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Add User error = $e")
                emit(Result.failure<WorkerDataResponse.WorkerData>(e))
            }
        }

    override fun removeUserFromFavourites(removeUserFromFavouritesRequest: RemoveUserFromFavouritesRequest): Flow<Result<WorkerDataResponse.WorkerData>> =
        flow {
            try {
                val response = api.removeUserFromFavourites(removeUserFromFavouritesRequest)

                if (response.code() == 200) response.body().let {
                    emit(Result.success(it!!.data) as Result<WorkerDataResponse.WorkerData>)
                } else {
                    Result.failure<WorkerDataResponse.WorkerData>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Remove User error = $e")
                emit(Result.failure<WorkerDataResponse.WorkerData>(e))
            }
        }

    override fun getOutsourceWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>> = flow {
        try {
            val response = api.getOutsourceList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersResponse.DataItem>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get Outsource Workers error = $e")
            emit(Result.failure<List<AllWorkersResponse.DataItem>>(e))
        }
    }

    override fun getPermissionsList(): Flow<Result<List<PermissionsListResponse.PermissionData>>> =
        flow {
            try {
                val response = api.getPermissionsList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<PermissionsListResponse.PermissionData>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get PermissionsList error = $e")
                emit(Result.failure<List<PermissionsListResponse.PermissionData>>(e))
            }
        }


    override fun getBelongsToUserPermissions(): Flow<Result<List<String>>> =
        flow {
            try {
                val response = api.getBelongsToUserPermissions()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<String>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get PermissionsList error = $e")
                emit(Result.failure<List<String>>(e))
            }
        }

    override fun deleteWorker(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = api.deleteWorker(id)
            if (response.code() == 204) {
                emit(Result.success(Unit))
            } else {
                emit(
                    Result.failure<Unit>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Delete worker error = $e")
            emit(Result.failure<Unit>(e))
        }
    }

    override fun updateContactData(updateContactDataRequest: UpdateContactDataRequest): Flow<Result<UpdateContactDataResponse.Data>> =
        flow {
            try {
                val response = api.updateContact(updateContactDataRequest)
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<UpdateContactDataResponse.Data>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Update Contact data error = $e")
                emit(Result.failure<UpdateContactDataResponse.Data>(e))
            }
        }

    override fun editWorkerData(
        id: Int,
        editWorkerRequest: EditWorkerRequest
    ): Flow<Result<EditWorkerDataResponse.Data>> =
        flow {
            try {
                val response = api.editWorker(id = id, editWorkerRequest = editWorkerRequest)
                if (response.code() == 200) response.body()?.let {
                    if (it.data != null) {
                        emit(Result.success(it.data))
                    }
                } else emit(
                    Result.failure<EditWorkerDataResponse.Data>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Edit worker data error = $e")
                emit(Result.failure<EditWorkerDataResponse.Data>(e))
            }
        }

    override fun getPositionList(): Flow<Result<List<AllPositionsResponse.PositionDataItem>>> =
        flow {
            try {
                val response = api.getPositionsList()
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it.data))
                } else emit(
                    Result.failure<List<AllPositionsResponse.PositionDataItem>>(
                        HttpException(response)
                    )
                )
            } catch (e: Exception) {
                timberLog("Get Position List error = $e")
                emit(Result.failure<List<AllPositionsResponse.PositionDataItem>>(e))
            }
        }

    override suspend fun getTeamWorkersShort() = try {
        val response = api.getTeamWorkersShort()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getAllWorkersShort() = try {
        val response = api.getAllWorkersShort()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getStructureShort() = try {
        val response = api.getStructureShort()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data?.structureList ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}



