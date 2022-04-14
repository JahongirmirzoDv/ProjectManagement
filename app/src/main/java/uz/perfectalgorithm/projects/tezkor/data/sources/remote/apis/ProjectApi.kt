package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.CreateProjectResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjecttttResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectsByStatus



interface
ProjectApi {

    @GET("/api/v1/project/project-list/")
    suspend fun getProjects(): Response<ProjecttttResponse>

    @Multipart
    @POST("/api/v1/project/project/")
    suspend fun createProject(
        @Part("title") title: RequestBody,
//        @Part("description") description: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("goal") goal: RequestBody,
        @Part("importance") importance: RequestBody,
//        @Part("yaqm") yaqm: RequestBody,
//        @Part("participants") participants: Array<RequestBody>?,
        @Part("spectators") spectators: Array<RequestBody>?,
//        @Part("functional_group") functional_group: Array<RequestBody>?,
        @Part("is_editable") canEditTime: RequestBody,
        @Part("repeat") repeat: RequestBody,
        @Part("project_plans") plans: Array<RequestBody>,
        @Part("repeat_rule") repeatRule: RequestBody?,
        @Part("until_date") reminderDate: RequestBody?,
        @Part("discussed_topic") topic: RequestBody?,
        @Part("plan") plan: RequestBody?
    ): Response<CreateProjectResponse>


    @GET("/api/v1/project/project-list-by-status/")
    suspend fun getProjectsByStatus(): Response<List<ProjectsByStatus>>

    @PATCH("/api/v1/project/project-update/{id}/")
    suspend fun changeProjectStatus(
        @Path("id") projectId: Int,
        @Body changeStatusRequest: ChangeStatusRequest
    ): Response<Any>

    @GET("/api/v1/project/project-retrieve/{id}/")
    suspend fun getProjectById(@Path("id") projectId: Int): Response<ProjectDetails>

    @DELETE("/api/v1/project/project-delete/{id}/")
    suspend fun deleteProject(@Path("id") projectId: Int): Response<Any>

    @Multipart
    @PATCH("/api/v1/project/project-update/{id}/")
    suspend fun updateProject(
        @Path("id") projectId: Int,
        @Part("title") title: RequestBody,
//        @Part("description") description: RequestBody?,
//        @Part("yaqm") yaqm: RequestBody?,
        @Part("performer") performer: RequestBody,
        @Part("leader") leader: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("deleted_files") deletedFiles: Array<RequestBody>,
        @Part("goal") goal: RequestBody,
        @Part("status") status: RequestBody,
        @Part("importance") importance: RequestBody,
//        @Part("participants") participants: Array<RequestBody>,
        @Part("spectators") spectators: Array<RequestBody>,
//        @Part("functional_group") functional_group: Array<RequestBody>,
        @Part("internal_status") internalStatus: RequestBody?,
        @Part("comment") comment: RequestBody?,
        @Part("is_editable") canEditTime: RequestBody,
        @Part("project_plans") newPlans: Array<RequestBody>,
        @Part("remove_project_plans") deletedPlans: Array<RequestBody>,
        @Part("repeat") repeat: RequestBody?,
        @Part("repeat_rule") repeatRule: RequestBody?,
        ): Response<Any>
}