package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionResponse


interface RepetitionApi {

    @GET("/api/v1/task/repeat-list")
    suspend fun getRepetition(): Response<RepetitionResponse>
}