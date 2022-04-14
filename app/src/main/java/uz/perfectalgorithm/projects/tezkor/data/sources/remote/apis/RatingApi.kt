package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.GetRatingIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RateIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingDataBody


interface RatingApi {

    @GET("/api/v1/idea/idea/{id}/")
    suspend fun getRatingIdea(
        @Path("id") id: Int
    ): Response<GetRatingIdeaResponse>

    @POST("/api/v1/idea/idea-rate/{id}/")
    suspend fun rateIdea(
        @Path("id") id: Int,
        @Body ratedBody: RatingDataBody
    ): Response<RateIdeaResponse>

}