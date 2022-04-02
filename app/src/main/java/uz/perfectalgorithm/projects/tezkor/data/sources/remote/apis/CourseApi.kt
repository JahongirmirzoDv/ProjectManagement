package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.CourseResponseItem

/**
 * Created by Jasurbek Kurganbaev on 8/20/2021 11:48 PM
 **/
interface CourseApi {

    @GET("/oz/arkhiv-kursov-valyut/json/USD/{date}/")
    suspend fun getCourse(
        @Path("date") date: String
    ): Response<List<CourseResponseItem>>

}