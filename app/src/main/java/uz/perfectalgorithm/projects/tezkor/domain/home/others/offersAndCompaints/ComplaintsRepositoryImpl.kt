package uz.perfectalgorithm.projects.tezkor.domain.home.others.offersAndCompaints

import android.net.Uri
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.OffersApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.WorkersApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BaseOffersItemResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers.BasePostOfferResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class ComplaintsRepositoryImpl @Inject constructor(
    private val offersApi: OffersApi,
    private val workersApi: WorkersApi
) : ComplaintsRepository, CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun getOffersAndComplaints(): Flow<Result<List<BaseOfferResponse.DataResult>>> =
        flow {
            try {
                val responseComplaints = async { offersApi.getAllComplaints() }
                val responseOffers = async { offersApi.getAllOffers() }
                val allOffersApi = responseOffers.await()
                val allComplaintsApi = responseComplaints.await()
                val allOffersAndComplaints = mutableListOf<BaseOfferResponse.DataResult>()

                allComplaintsApi.body()?.data?.let {
                    it.all { result ->
                        result.type = App.instance.getString(R.string.complaint_)
                        return@all true
                    }
                    allOffersAndComplaints.addAll(it)
                }
                allOffersApi.body()?.data?.let {
                    it.all { result ->
                        result.type = App.instance.getString(R.string.offer)
                        return@all true
                    }
                    allOffersAndComplaints.addAll(it)
                }

                Collections.sort(
                    allOffersAndComplaints, kotlin.Comparator { o1, o2 ->
                        return@Comparator DateUtil.typeConverterDateByLong(o1.createdAt)
                            .compareTo(
                                DateUtil.typeConverterDateByLong(o2.createdAt)
                            )
                    }
                )

                if (allComplaintsApi.code() == 200 && allOffersApi.code() == 200)
                    emit(
                        Result.success(
                            allOffersAndComplaints
                        )
                    ) else {
                    emit(
                        Result.failure<List<BaseOfferResponse.DataResult>>(
                            if (!allComplaintsApi.isSuccessful) HttpException(allComplaintsApi) else HttpException(
                                allOffersApi
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                emit(
                    Result.failure<List<BaseOfferResponse.DataResult>>(
                        e
                    )
                )
            }
        }

    override fun getComplaintItem(id: Int): Flow<Result<BaseOffersItemResponse.DataResult>> = flow {
        try {
            val response = offersApi.getComplaintItem(id)
            if (response.code() == 200) response.body()?.let {
                it.data.type = App.instance.getString(R.string.complaint_)
                emit(Result.success(it.data))
            } else {
                emit(
                    Result.failure<BaseOffersItemResponse.DataResult>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                Result.failure<BaseOffersItemResponse.DataResult>(
                    e
                )
            )
        }
    }

    override fun postComplaint(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ): Flow<Result<BasePostOfferResponse.DataResult>> = flow {
        try {
            val spectatorBody = Array(spectators.size) {
                spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in files.indices) {
                filesBody.add(prepareImageFilePart("files", files[i]))
            }


            val response = if (to != null) {
                offersApi.postComplaint(
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = filesBody,
                    spectators = spectatorBody,
                    toCompany = toCompany.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    to = to.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                )



            } else {
                offersApi.postComplaint(
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = filesBody,
                    spectators = spectatorBody,
                    toCompany = toCompany.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    null,
                )
            }
            if (response.code() == 201) response.body()?.let {
                emit(Result.success(it.data[0]))
            } else {
                emit(
                    Result.failure<BasePostOfferResponse.DataResult>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("add Orders and Complaints error = $e")
            emit(Result.failure<BasePostOfferResponse.DataResult>(e))
        }
    }

    private fun prepareImageFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file
            .asRequestBody(
                App.instance.contentResolver.getType(Uri.fromFile(file))?.toMediaTypeOrNull()
            )

        return MultipartBody.Part.createFormData(
            partName, file.name, requestFile
        )
    }


    override fun getOffers(): Flow<Result<List<BaseOfferResponse.DataResult>>> = flow {
        try {
            val response = offersApi.getAllOffers()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else {
                emit(
                    Result.failure<List<BaseOfferResponse.DataResult>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: IOException) {
            emit(
                Result.failure<List<BaseOfferResponse.DataResult>>(
                    e
                )
            )
        }
    }

    override suspend fun getOffersNew() = try {
        val response = offersApi.getAllOffers()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getComplaintsNew() = try {
        val response = offersApi.getAllComplaints()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override fun getComplaints(): Flow<Result<List<BaseOfferResponse.DataResult>>> = flow {
        try {
            val response = offersApi.getAllComplaints()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else {
                emit(
                    Result.failure<List<BaseOfferResponse.DataResult>>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: IOException) {
            emit(
                Result.failure<List<BaseOfferResponse.DataResult>>(
                    e
                )
            )
        }
    }

    override fun getOfferItem(id: Int): Flow<Result<BaseOffersItemResponse.DataResult>> = flow {
        try {
            val response = offersApi.getOfferItem(id)
            if (response.code() == 200) response.body()?.let {
                it.data.type = App.instance.getString(R.string.offer)
                emit(Result.success(it.data))
            } else {
                emit(
                    Result.failure<BaseOffersItemResponse.DataResult>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: IOException) {
            emit(
                Result.failure<BaseOffersItemResponse.DataResult>(
                    e
                )
            )
        }
    }

    override fun postOffer(
        description: String,
        files: List<File>,
        spectators: List<Int>,
        to: Int?,
        toCompany: Boolean
    ): Flow<Result<BasePostOfferResponse.DataResult>> = flow {
        try {
            val spectatorBody = Array(spectators.size) {
                spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }
            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in files.indices) {
                filesBody.add(prepareImageFilePart("files", files[i]))
            }

            val response = if (to != null) {
                offersApi.postOffer(
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = filesBody,
                    spectators = spectatorBody,
                    toCompany = toCompany.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    to = to.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                )
            } else {
                offersApi.postOffer(
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = filesBody,
                    spectators = spectatorBody,
                    toCompany = toCompany.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    null,
                )
            }
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data[0]))
            } else {
                emit(
                    Result.failure<BasePostOfferResponse.DataResult>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.failure<BasePostOfferResponse.DataResult>(e))
        }
    }

    override fun getAllWorkers(): Flow<Result<List<AllWorkersResponse.DataItem>>> = flow {
        try {
            val response = workersApi.getAllWorkers()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it.data))
            } else emit(
                Result.failure<List<AllWorkersResponse.DataItem>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("Get All Workers error = $e")
            emit(Result.failure<List<AllWorkersResponse.DataItem>>(e))
        }
    }


    override fun deleteOffer(id: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = offersApi.deleteOffer(id)
            if (response.code() == 204) {
                emit(Result.success(true))
            } else emit(
                Result.failure<Boolean>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<Boolean>(e))
        }
    }

    override fun deleteComplaint(id: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = offersApi.deleteComplaint(id)
            if (response.code() == 204) {
                emit(Result.success(true))
            } else emit(
                Result.failure<Boolean>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            emit(Result.failure<Boolean>(e))
        }
    }


}