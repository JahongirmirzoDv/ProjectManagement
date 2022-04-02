package uz.perfectalgorithm.projects.tezkor.domain.home.department

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.DepartmentApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreateDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePrimaryDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

class DepartmentRepositoryImpl @Inject constructor(private val departmentApi: DepartmentApi) :
    DepartmentRepository {


    override fun getDepartmentList(): Flow<Result<List<DepartmentListResponse.DepartmentDataItem>>> =
        flow {
            try {
                val response = departmentApi.getDepartmentList()
                if (response.code() == 200) response.body().let {
                    emit(Result.success(it!!.data))
                } else {
                    Result.failure<List<DepartmentListResponse.Result>>(
                        HttpException(response)
                    )
                }

            } catch (e: Exception) {
                timberLog("Get DepartmentList error = $e")
                emit(Result.failure<List<DepartmentListResponse.DepartmentDataItem>>(e))
            }
        }

    override fun createDepartment(createDepartmentRequest: CreateDepartmentRequest): Flow<Result<CreateDepartmentResponse.DepartmentData>> =
        flow {
            try {
                val response = departmentApi.createDepartment(createDepartmentRequest)

                if (response.code() == 201) response.body().let {
                    emit(Result.success(it!!.data) as Result<CreateDepartmentResponse.DepartmentData>)
                } else {
                    Result.failure<CreateDepartmentResponse.DepartmentData>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Department error = $e")
                emit(Result.failure<CreateDepartmentResponse.DepartmentData>(e))
            }
        }

    override fun createPrimaryDepartment(createPrimaryDepartmentRequest: CreatePrimaryDepartmentRequest): Flow<Result<CreateDepartmentResponse.DepartmentData>> =
        flow {
            try {
                val response = departmentApi.createPrimaryDepartment(createPrimaryDepartmentRequest)

                if (response.code() == 201) response.body().let {
                    emit(Result.success(it!!.data) as Result<CreateDepartmentResponse.DepartmentData>)
                } else {
                    Result.failure<CreateDepartmentResponse.DepartmentData>(
                        HttpException(response)
                    )
                }
            } catch (e: Exception) {
                timberLog("Create Department error = $e")
                emit(Result.failure<CreateDepartmentResponse.DepartmentData>(e))
            }
        }

    override fun updateDepartment(
        id: Int,
        updateDepartmentRequest: CreatePrimaryDepartmentRequest
    ): Flow<Result<Any>> = flow {
        try {
            val response = departmentApi.updateDepartment(id, updateDepartmentRequest)
            if (response.isSuccessful) {
                emit(Result.success(response.body() ?: Any()))
            } else {
                Result.failure<CreateDepartmentResponse.DepartmentData>(
                    HttpException(response)
                )
            }
        } catch (e: Exception) {
            timberLog("Create Department error = $e")
            emit(Result.failure<CreateDepartmentResponse.DepartmentData>(e))
        }
    }

    override fun deleteDepartment(id: Int): Flow<Result<Any>> =
        flow {
            try {
                val response = departmentApi.deleteDepartment(id)
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


}