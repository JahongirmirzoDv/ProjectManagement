package uz.perfectalgorithm.projects.tezkor.domain.home.rating_idea

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.RatingApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.GetRatingIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RateIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingDataBody
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 02.08.2021 11:24
 **/
class RatingRepositoryImpl @Inject constructor(
    private val ratingApi: RatingApi
) : RatingRepository {

    override fun getRatingIdea(id: Int): Flow<Result<GetRatingIdeaResponse>> = flow {
        try {
            val response = ratingApi.getRatingIdea(id)
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<GetRatingIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<GetRatingIdeaResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<GetRatingIdeaResponse>(e))
        }
    }

    override fun rateIdea(id: Int, data: RatingDataBody) = flow {
        try {
            val response = ratingApi.rateIdea(id, data)
            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<RateIdeaResponse>(
                            Exception(
                                App.instance.resources.getString(
                                    R.string.error
                                )
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure<RateIdeaResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<RateIdeaResponse>(e))
        }
    }
}