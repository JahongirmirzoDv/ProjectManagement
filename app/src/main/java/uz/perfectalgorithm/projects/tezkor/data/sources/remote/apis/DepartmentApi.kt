package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreateDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.CreatePrimaryDepartmentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.CreateDepartmentResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.DepartmentListResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse

interface DepartmentApi {

    @GET("/api/v1/company/structure")
    suspend fun getStructure(): Response<StructureResponse.Result>

    @GET("/api/v1/company/department/")
    suspend fun getDepartmentList(): Response<DepartmentListResponse.Result>

    @POST("/api/v1/company/department-create/")
    suspend fun createDepartment(
        @Body createDepartmentRequest: CreateDepartmentRequest
    ): Response<CreateDepartmentResponse.Result>

    @POST("/api/v1/company/department-create/")
    suspend fun createPrimaryDepartment(
        @Body createPrimaryDepartmentRequest: CreatePrimaryDepartmentRequest
    ): Response<CreateDepartmentResponse.Result>

    @PUT("/api/v1/company/department-update/{id}/")
    suspend fun updateDepartment(
        @Path("id") id: Int,
        @Body createPrimaryDepartmentRequest: CreatePrimaryDepartmentRequest
    ): Response<Any>

    @DELETE("/api/v1/company/department-delete/{id}/")
    suspend fun deleteDepartment(
        @Path("id") id: Int,
    ): Response<Any>


}