package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusResponse

/**
 * Created by Jasurbek Kurganbaev on 10.07.2021 11:08
 **/
interface ProjectStatusApi {

    @GET("/api/v1/project/project-status/")
    suspend fun getStatusList(): Response<StatusResponse>

    @POST("api/v1/project/project-status/")
    suspend fun createStatus(
        @Body createStatusRequest: CreateStatusRequest
    ): Response<Any>

    @PUT("api/v1/project/project-status-update/{id}/")
    suspend fun updateStatus(
        @Path("id") statusId: Int,
        @Body createStatusRequest: CreateStatusRequest
    ): Response<Any>

    @DELETE("api/v1/project/project-status-delete/{id}/")
    suspend fun deleteStatus(
        @Path("id") statusId: Int
    ): Response<Any>
}