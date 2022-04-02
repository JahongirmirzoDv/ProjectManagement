package uz.perfectalgorithm.projects.tezkor.domain.home.chat.channel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ChannelApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.channel.ChannelData
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 18.06.2021 15:41
 **/
class ChannelRepositoryImpl @Inject constructor(
    private val channelApi: ChannelApi,
    private val storage: LocalStorage
) : ChannelRepository {
    override fun getChannels(): Flow<Result<List<ChannelData>>> = flow {
        try {
            val response = channelApi.getChannels()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<ChannelData>>(HttpException(response))
            )
        } catch (e: Exception) {
            timberLog("ChannelRepositoryImpl in function refreshToken error = $e")
            emit(Result.failure<List<ChannelData>>(e))
        }
    }


}