package uz.perfectalgorithm.projects.tezkor.domain.home.chat.channel

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData

/**
 * Created by Jasurbek Kurganbaev on 18.06.2021 15:41
 **/
interface ChannelRepository {

    fun getChannels(): Flow<Result<List<ChannelData>>>

}