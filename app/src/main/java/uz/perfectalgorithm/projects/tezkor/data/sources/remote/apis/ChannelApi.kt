package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelResponse

/**
 * Created by Jasurbek Kurganbaev on 18.06.2021 15:24
 **/
interface ChannelApi {

    @GET("/api/v1/chat/chat-channel/")
    suspend fun getChannels(): Response<ChannelResponse>

}