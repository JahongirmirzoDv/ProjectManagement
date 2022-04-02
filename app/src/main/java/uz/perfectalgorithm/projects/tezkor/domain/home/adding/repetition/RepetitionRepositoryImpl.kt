package uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.RepetitionApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 14.07.2021 16:53
 **/
class RepetitionRepositoryImpl @Inject constructor(
    private val repetitionApi: RepetitionApi
) : RepetitionRepository {

    override suspend fun getRepetitionsNew() = try {
        val response = repetitionApi.getRepetition()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getRepetitions(): Flow<Result<List<RepetitionData>>> = flow {
        try {
            val response = repetitionApi.getRepetition()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<List<RepetitionData>>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Result.failure<List<RepetitionData>>(e))
        }

    }
}