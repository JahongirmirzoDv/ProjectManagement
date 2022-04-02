package uz.perfectalgorithm.projects.tezkor.domain.home.department

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreateDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePrimaryDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse

interface DepartmentRepository {

    fun getDepartmentList(): Flow<Result<List<DepartmentListResponse.DepartmentDataItem>>>

    fun createDepartment(createDepartmentRequest: CreateDepartmentRequest): Flow<Result<CreateDepartmentResponse.DepartmentData>>

    fun createPrimaryDepartment(createPrimaryDepartmentRequest: CreatePrimaryDepartmentRequest): Flow<Result<CreateDepartmentResponse.DepartmentData>>

    fun updateDepartment(
        id: Int,
        updateDepartmentRequest: CreatePrimaryDepartmentRequest
    ): Flow<Result<Any>>

    fun deleteDepartment(id: Int): Flow<Result<Any>>

}