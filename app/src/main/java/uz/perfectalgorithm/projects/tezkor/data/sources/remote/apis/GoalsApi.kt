package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.*


interface GoalsApi {

    @GET("/api/v1/goal/goal-folder/")
    suspend fun getGoalMaps(): Response<GoalFolderListResponse>

    @Multipart
    @POST("/api/v1/goal/goal/")
    suspend fun createGoal(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("folder") folder: RequestBody,
        @Part("status") status: RequestBody,
        @Part("participants") participants: Array<RequestBody>?,
        @Part("spectators") spectators: Array<RequestBody>?,
        @Part("functional_group") functional_group: Array<RequestBody>?,
        @Part("is_editable") canEditTime: RequestBody
    ): Response<CreateGoalResponse>

    @GET("/api/v1/goal/goal/")
    suspend fun getGoals(): Response<GoalResponse>

    @FormUrlEncoded
    @POST("/api/v1/goal/goal-folder/")
    suspend fun createGoalMap(@Field("title") title: String): Response<GoalFolderResponse>

    @GET("/api/v1/goal/goal-folder/{id}/")
    suspend fun getGoalMap(
        @Path("id") id: Int
    ): Response<GoalMapSubObjectsResponse>

    @GET("/api/v1/goal/goal-retrieve/{id}/")
    suspend fun getGoalById(
        @Path("id") goalId: Int
    ): Response<GoalDetails>

    @DELETE("/api/v1/goal/goal-delete/{id}/")
    suspend fun deleteGoal(
        @Path("id") goalId: Int
    ): Response<Any>

    @Multipart
    @PATCH("/api/v1/goal/goal-update/{id}/")
    suspend fun updateGoal(
        @Path("id") goalId: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("deleted_files") deletedFiles: Array<RequestBody>,
        @Part("folder") folder: RequestBody,
        @Part("status") status: RequestBody,
        @Part("participants") participants: Array<RequestBody>,
        @Part("spectators") spectators: Array<RequestBody>,
        @Part("functional_group") functional_group: Array<RequestBody>,
        @Part("is_editable") canEditTime: RequestBody
    ): Response<Any>
}