package uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BasePostOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import java.io.File

interface ComplaintsRepository {
    fun getComplaints(): Flow<Result<List<BaseOfferResponse.DataResult>>>
    fun getOffers(): Flow<Result<List<BaseOfferResponse.DataResult>>>

    suspend fun getComplaintsNew(): DataWrapper<List<BaseOfferResponse.DataResult>>
    suspend fun getOffersNew(): DataWrapper<List<BaseOfferResponse.DataResult>>

    fun getOffersAndComplaints(): Flow<Result<List<BaseOfferResponse.DataResult>>>
    fun getComplaintItem(id: Int): Flow<Result<BaseOffersItemResponse.DataResult>>
    fun postComplaint(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ): Flow<Result<BasePostOfferResponse.DataResult>>

    fun getOfferItem(id: Int): Flow<Result<BaseOffersItemResponse.DataResult>>

    fun postOffer(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ): Flow<Result<BasePostOfferResponse.DataResult>>

    fun getAllWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>>


    fun deleteOffer(id: Int): Flow<Result<Boolean>>

    fun deleteComplaint(id: Int): Flow<Result<Boolean>>
}