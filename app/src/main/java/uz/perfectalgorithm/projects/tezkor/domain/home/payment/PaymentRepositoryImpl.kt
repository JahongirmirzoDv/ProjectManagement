package uz.perfectalgorithm.projects.tezkor.domain.home.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.CourseApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.PaymentApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.*
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 8/20/2021 10:27 AM
 **/
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi,
    private val courseApi: CourseApi
) : PaymentRepository {
    override fun postOrder(amount: Int): Flow<Result<PostOrderResponse>> = flow {

        try {
            val response = paymentApi.postOrder(/*body*/amount)
            if (response.code() == 201) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(
                    Result.failure<PostOrderResponse>(
                        HttpException(response)
                    )
                )
            }
        } catch (e: Exception) {
            timberLog("Get Course error = $e")
            emit(Result.failure<PostOrderResponse>(e))
        }

    }

    override fun paymentUrl(/*body: OrderIdBody*/orderId: Int): Flow<Result<PaymentUrlResponse>> =
        flow {

            try {
                val response = paymentApi.paymentUrl(/*body*/orderId)
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it))
                } else {
                    emit(
                        Result.failure<PaymentUrlResponse>(
                            HttpException(response)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Course error = $e")
                emit(Result.failure<PaymentUrlResponse>(e))
            }
        }

    override fun getCourse(date: String): Flow<Result<CourseResponseItem>> =
        flow {
            try {
                val response = courseApi.getCourse(date)
                if (response.code() == 200) response.body()?.let {
                    emit(Result.success(it[0]))
                } else {
                    emit(
                        Result.failure<CourseResponseItem>(
                            HttpException(response)
                        )
                    )
                }
            } catch (e: Exception) {
                timberLog("Get Course error = $e")
                emit(Result.failure<CourseResponseItem>(e))
            }
        }
}