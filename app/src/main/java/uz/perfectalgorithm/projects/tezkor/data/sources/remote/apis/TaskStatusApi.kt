package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.status.CreateStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusResponse



interface TaskStatusApi {

    @GET("/api/v1/task/task-status/")
    suspend fun getStatusList(): Response<StatusResponse>

    @POST("api/v1/task/task-status/")
    suspend fun createStatus(
        @Body createStatusRequest: CreateStatusRequest
    ): Response<Any>

    @PUT("api/v1/task/task-status-update/{id}/")
    suspend fun updateStatus(
        @Path("id") statusId: Int,
        @Body createStatusRequest: CreateStatusRequest
    ): Response<Any>

    @DELETE("api/v1/task/task-status-delete/{id}/")
    suspend fun deleteStatus(
        @Path("id") statusId: Int
    ): Response<Any>
}