package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse

/**
 * Created by Davronbek Raximjanov on 21.06.2021
 **/

interface OthersApi {
    @GET("/api/v1/user/staff-retrieve/{id}/")
    suspend fun getUserData(
        @Path("id") id: Int
    ): Response<UserDataResponse.Result>

    @PATCH("/api/v1/user/profile-update/")
    suspend fun updateUserData(
        @Body updateUserDataRequest: UpdateUserDataRequest
    ): Response<UpdateUserDataResponse.Result>

    @Multipart
    @PATCH("/api/v1/user/profile-update/")
    suspend fun updateUserDataWithImage(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("birth_date") birthDate: RequestBody,
        @Part part: MultipartBody.Part,
    ): Response<UpdateUserDataResponse.Result>

    @DELETE("/api/v1/notification/device-delete/")
    suspend fun logout(): Response<Any>

}