package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder.CreateFolderRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.*

/**
 * Created by Jasurbek Kurganbaev on 26.06.2021 10:42
 **/
interface TasksApi {

    @Multipart
    @POST("/api/v1/task/task/")
    suspend fun createTask(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("yaqm") yaqm: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("project") project: RequestBody?,
        @Part("parent") parent: RequestBody?,
        @Part("folder") folder: RequestBody,
        @Part("importance") importance: RequestBody,
        @Part("participants") participants: Array<RequestBody>?,
        @Part("spectators") spectators: Array<RequestBody>?,
        @Part("functional_group") functional_group: Array<RequestBody>?,
        @Part("repeat") repeat: RequestBody,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("repeat_exceptions") repeatExceptions: @JvmSuppressWildcards List<RequestBody>?,
        @Part("is_editable") canEditTime: RequestBody?,
        @Part("until_date") reminderDate: RequestBody?,
        @Part("discussed_topic") topic: RequestBody?,
        @Part("plan") plan: RequestBody?,
        @Part("is_calendar") isShowCalendar: RequestBody?
    ): Response<CreateTaskResponse>

    @Multipart
    @POST("/api/v1/task/task/")
    suspend fun createTaskFromMessage(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("yaqm") yaqm: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("project") project: RequestBody?,
        @Part("parent") parent: RequestBody?,
        @Part("folder") folder: RequestBody,
        @Part("importance") importance: RequestBody,
        @Part("participants") participants: Array<RequestBody>?,
        @Part("spectators") spectators: Array<RequestBody>?,
        @Part("functional_group") functional_group: Array<RequestBody>?,
        @Part("repeat") repeat: RequestBody,
        @Part("message_id") messageId: RequestBody,
        @Part("is_editable") canEditTime: RequestBody?,
        @Part("until_date") reminderDate: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("discussed_topic") topic: RequestBody?,
        @Part("plan") plan: RequestBody?,
        @Part("is_calendar")  isShowCalendar: RequestBody?
    ): Response<CreateTaskResponse>


    @POST("api/v1/task/task-folder/")
    suspend fun createTaskFolder(
        @Body createFolderRequest: CreateFolderRequest
    ): Response<Any>

    @PUT("api/v1/task/task-folder-update/{id}/")
    suspend fun updateTaskFolder(
        @Path("id") folderId: Int,
        @Body createFolderRequest: CreateFolderRequest
    ): Response<Any>

    @DELETE("/api/v1/task/task-folder-delete/{id}/")
    suspend fun deleteTaskFolder(@Path("id") folderId: Int): Response<Any>

    @GET("/api/v1/task/task-folder-list/")
    suspend fun getFunctionalFolderList(): Response<List<TaskFolderListItem>>

    @GET("/api/v1/task/task-list/")
    suspend fun getAllFunctionalList(): Response<List<FolderItem>>

    @GET("/api/v1/task/own-task/")
    suspend fun getOwnTaskList(): Response<OwnTaskListResponse>

    @PATCH("/api/v1/task/task-update/{id}/")
    suspend fun changeTaskStatus(
        @Path("id") taskId: Int,
        @Body changeStatusRequest: ChangeStatusRequest
    ): Response<Any>

    @GET("/api/v1/task/task-retrieve/{id}/")
    suspend fun getTaskById(@Path("id") taskId: Int): Response<TaskDetails>

    @DELETE("/api/v1/task/task-delete/{id}/")
    suspend fun deleteTask(@Path("id") taskId: Int): Response<Any>

    @FormUrlEncoded
    @PATCH("/api/v1/quick-plan/quick-plan-update/{id}/")
    suspend fun sendTaskTrue(@Path("id") taskId: Int, @Field("task_status") status: Boolean = true): Response<SendTaskTrue>

    @Multipart
    @PATCH("/api/v1/task/task-update/{id}/")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("yaqm") yaqm: RequestBody?,
        @Part("performer") performer: RequestBody?,
        @Part("leader") leader: RequestBody?,
        @Part("start_time") start_time: RequestBody?,
        @Part("end_time") end_time: RequestBody?,
        @Part files: List<MultipartBody.Part>?,
        @Part("deleted_files") deletedFiles: Array<RequestBody>?,
        @Part("project") project: RequestBody?,
        @Part("parent") parent: RequestBody?,
        @Part("folder") folder: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("importance") importance: RequestBody?,
        @Part("participants") participants: Array<RequestBody>?,
        @Part("spectators") spectators: Array<RequestBody>?,
        @Part("functional_group") functional_group: Array<RequestBody>?,
        @Part("repeat") repeat: RequestBody?,
        @Part("internal_status") internalStatus: RequestBody?,
        @Part("comment") comment: RequestBody?,
        @Part("is_editable") canEditTime: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        ): Response<Any>

    @POST("api/v1/task/task-change-history/{id}/")
    suspend fun updateHistory(@Path("id")taskId: Int,
    @Body request:HashMap<String,Any?>):Response<Any>

    @Multipart
    @POST("/api/v1/task/task/")
    suspend fun createSimpleTask(
        @Part("title") title: RequestBody,
        @Part("folder") folder: RequestBody,
        @Part("status") status: RequestBody,
    ): Response<Any>

    @GET("/api/v1/task/task-commentaries/{id}/")
    suspend fun getTaskAllComments(@Path("id") taskId: Int): Response<List<TaskCommentData>>

    @POST("/api/v1/task/task-comment/")
    suspend fun postTaskComment(
        @Body taskCommentRequest: TaskCommentRequest
    ): Response<PostTaskCommentResponse>
}