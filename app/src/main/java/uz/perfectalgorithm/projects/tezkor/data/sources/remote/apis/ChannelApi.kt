package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.GET
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelResponse

interface ChannelApi {

    @GET("/api/v1/chat/chat-channel/")
    suspend fun getChannels(): Response<ChannelResponse>

}