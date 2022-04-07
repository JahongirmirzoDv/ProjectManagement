package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BasePostOfferResponse

/***
 * Bu taklif va shikoyatlar qismi uchun api
 */


interface OffersApi {

    @GET("/api/v1/application/application-list/")
    suspend fun getAllOffers(): Response<BaseOfferResponse.Result>

    @GET("/api/v1/complaint/complaint-list/")
    suspend fun getAllComplaints(): Response<BaseOfferResponse.Result>


    @GET("/api/v1/application/application/{id}/")
    suspend fun getOfferItem(@Path("id") id: Int): Response<BaseOffersItemResponse.Result>

    @GET("/api/v1/complaint/complaint-retrieve/{id}/")
    suspend fun getComplaintItem(@Path("id") id: Int): Response<BaseOffersItemResponse.Result>

    @Multipart
    @POST("/api/v1/application/application/")
    suspend fun postOffer(
        @Part("description") description: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("spectators") spectators: Array<RequestBody>,
        @Part("to_company") toCompany: RequestBody,
        @Part("to") to: RequestBody?
    ): Response<BasePostOfferResponse.Result>

    @Multipart
    @POST("/api/v1/complaint/complaint/")
    suspend fun postComplaint(
        @Part("description") description: RequestBody,
        @Part files: List<MultipartBody.Part>,
        @Part("spectators") spectators: Array<RequestBody>,
        @Part("to_company") toCompany: RequestBody,
        @Part("to") to: RequestBody?
    ): Response<BasePostOfferResponse.Result>


    @DELETE("/api/v1/complaint/complaint-delete/{id}/")
    suspend fun deleteComplaint(@Path("id") id: Int): Response<Any>

    @DELETE("/api/v1/application/application-delete/{id}/")
    suspend fun deleteOffer(@Path("id") id: Int): Response<Any>

}