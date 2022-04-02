package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_idea.*

/**
 * Created by Jasurbek Kurganbaev on 17.07.2021 14:10
 **/
interface QuickIdeasApi {

    @FormUrlEncoded
    @POST("/api/v1/idea/idea-folder/")
    suspend fun createIdeasBox(@Field("title") title: String): Response<CreateQuickIdeaBoxResponse>

    @DELETE("/api/v1/idea/idea-folder-delete/{id}/")
    suspend fun deleteIdeaBox(@Path("id") id: Int): Response<DeleteQuickIdeaBoxResponse>

    @PUT("/api/v1/idea/idea-folder-update/{id}/")
    suspend fun updateIdeaBox(
        @Path("id") id: Int,
        @Body data: UpdateQuickIdeasBoxData
    ): Response<CreateQuickIdeaBoxResponse>

    @Multipart
    @POST("/api/v1/idea/idea/")
    suspend fun createQuickIdea(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part files: List<MultipartBody.Part>,
        @Part("folder") folder: RequestBody,
        ): Response<QuickIdeaResponse>

    @Multipart
    @POST("/api/v1/idea/idea/")
    suspend fun createQuickIdeaWithin(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part files: List<MultipartBody.Part>,
        @Part("to_idea_box") to_idea_box: RequestBody
    ): Response<QuickIdeaResponse>


    @GET("/api/v1/idea/idea/{id}/")
    suspend fun getQuickIdea(
        @Path("id") id: Int
    ): Response<GetQuickIdeaResponse>

    @Multipart
    @PUT("/api/v1/idea/idea-update/{id}/")
    suspend fun updateQuickIdea(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part files: List<MultipartBody.Part>,
        @Part("folder") folder: RequestBody,
        @Part("to_idea_box") to_idea_box: RequestBody
    ): Response<UpdateQuickIdeaResponse>

    @DELETE("/api/v1/idea/idea-delete/{id}/")
    suspend fun deleteIdea(@Path("id") id: Int): Response<DeleteQuickIdeaResponse>

    @GET("/api/v1/idea/idea-folder/")
    suspend fun getAllIdeaBoxes(): Response<GetQuickIdeasBoxesResponse>

    @GET("/api/v1/idea/idea-list-by-rate/{id}/")
    suspend fun getIdeasByRating(@Path("id") id: Int): Response<CategoryIdeaResponse>

    /*  @GET("/api/v1/idea/idea-list-finished/{id}/")
      suspend fun getIdeasByFinished(@Path("id") id: Int): Response<CategoryIdeaByFinishedResponse>
 */

    @GET("/api/v1/idea/idea-box/")
    suspend fun getGeneralIdeas(): Response<List<RatingIdeaData>>

    @GET("/api/v1/idea/idea-box-finished/")
    suspend fun getGeneralFinishedIdeas(): Response<List<FinishedIdeaItem>>


}
