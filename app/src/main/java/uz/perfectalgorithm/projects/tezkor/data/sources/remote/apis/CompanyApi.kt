package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.CreateCompanyResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse

/***
 * Abduraxmonov Abdulla 11/09/2021
 * Bu company qismi uchun api
 */


interface CompanyApi {

    @Multipart
    @POST("/api/v1/company/company-create/")
    suspend fun createCompany(
        @Part("title") title: RequestBody,
        @Part("direction") description: RequestBody,
        @Part("username") bankName: RequestBody,
        @Part("email") email: RequestBody,
        @Part part: MultipartBody.Part? = null,
    ): Response<CreateCompanyResponse.Result>


    @GET("/api/v1/company/company-direction/")
    suspend fun getDirection(): Response<List<DirectionResponse.Direction>>

    @GET("/api/v1/company/company-subdirection/")
    suspend fun getDirection2(
        @Query("direction_id") id: Int
    ): Response<List<DirectionResponse.Direction>>
}