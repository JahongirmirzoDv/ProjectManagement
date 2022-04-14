package uz.perfectalgorithm.projects.tezkor.domain.home.rating_idea

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.GetRatingIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RateIdeaResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingDataBody


interface RatingRepository {

    fun getRatingIdea(id: Int): Flow<Result<GetRatingIdeaResponse>>

    fun rateIdea(id: Int, data: RatingDataBody): Flow<Result<RateIdeaResponse>>


}