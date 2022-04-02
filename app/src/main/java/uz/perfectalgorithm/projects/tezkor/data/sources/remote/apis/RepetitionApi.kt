package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionResponse

/**
 * Created by Jasurbek Kurganbaev on 14.07.2021 16:50
 **/
interface RepetitionApi {

    @GET("/api/v1/task/repeat-list")
    suspend fun getRepetition(): Response<RepetitionResponse>
}