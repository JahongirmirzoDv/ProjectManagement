package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.positons.AllPositionsResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.StructureShortResponse



interface WorkersApi {

    @GET("/api/v1/user/staff-retrieve/{id}/")
    suspend fun getStaff(
        @Path("id") staffId: Int
    ): Response<StaffResponse>

    @GET("/api/v1/company/user-favourites/")
    suspend fun getTeamWorkers(): Response<TeamWorkersResponse>

    @GET("/api/v1/company/short-user-favourites/")
    suspend fun getTeamWorkersShort(): Response<TeamWorkersShortResponse>

    @GET("/api/v1/user/personal")
    suspend fun getUserData(
    ): Response<UserDataResponse.Result>

    @GET("/api/v1/company/all-staffs/")
    suspend fun getAllWorkers(): Response<AllWorkersResponse.Result>

    @GET("/api/v1/company/short-all-staffs/")
    suspend fun getAllWorkersShort(): Response<AllWorkersShortResponse>

    @GET("/api/v1/company/structure/")
    suspend fun getStructure(): Response<StructureResponse.Result>

    @GET("/api/v1/company/short-structure/")
    suspend fun getStructureShort(): Response<StructureShortResponse>

    @POST("/api/v1/user/staff/")
    suspend fun createWorker(
        @Body createWorkerRequest: CreateWorkerRequest
    ): Response<CreateWorkerResponse.Result>

    @Multipart
    @POST("/api/v1/user/staff/")
    suspend fun createWorkerWithImage(
        @Part("phone_number") phone: RequestBody,
        @Part("position") positions: Array<RequestBody>,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part part: MultipartBody.Part,
        @Part("birth_date") dateOfBirth: RequestBody,
        @Part("password") password: RequestBody,
        @Part("sex") gender: RequestBody,
        @Part("is_outsource") isOutsource: RequestBody,
    ): Response<CreateWorkerResponse.Result>

    @GET("/api/v1/company/structure-wout-staff")
    suspend fun getStructureWithPositions(): Response<StructurePositionsResponse.Result>

    @POST("/api/v1/company/position-create/")
    suspend fun createPosition(
        @Body createPositionRequest: CreatePositionRequest
    ): Response<CreatePositionResponse.Result>

    @POST("/api/v1/user/add-favourite")
    suspend fun addUserToFavourites(
        @Body addUserToFavouritesRequest: AddUserToFavouritesRequest
    ): Response<WorkerDataResponse.Result>

    @POST("/api/v1/user/remove-favourite")
    suspend fun removeUserFromFavourites(
        @Body removeUserFromFavouritesRequest: RemoveUserFromFavouritesRequest
    ): Response<WorkerDataResponse.Result>

    @GET("/api/v1/user/outsource-list")
    suspend fun getOutsourceList(): Response<OutsourceWorkersResponse.Result>

    @GET("/api/v1/company/all-permissions")
    suspend fun getPermissionsList(): Response<PermissionsListResponse.Result>

    @GET("/api/v1/user/my-permissions/")
    suspend fun getBelongsToUserPermissions(): Response<UserPermissionsListResponse.Result>

    @DELETE("/api/v1/user/staff-delete/{id}/")
    suspend fun deleteWorker(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("/api/v1/user/contact-edit/")
    suspend fun updateContact(
        @Body updateContactDataRequest: UpdateContactDataRequest
    ): Response<UpdateContactDataResponse.Result>

    @PATCH("/api/v1/user/staff-update/{id}/")
    suspend fun editWorker(
        @Path("id") id: Int,
        @Body editWorkerRequest: EditWorkerRequest
    ): Response<EditWorkerDataResponse.ResponseData>

    @GET("/api/v1/company/position/")
    suspend fun getPositionsList(): Response<AllPositionsResponse.Result>

}
