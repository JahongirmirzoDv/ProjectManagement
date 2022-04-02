package uz.perfectalgorithm.projects.tezkor.domain.home.payment

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.*

/**
 * Created by Jasurbek Kurganbaev on 8/20/2021 10:27 AM
 **/
interface PaymentRepository {

    fun postOrder(/*body: OrderBody*/amount: Int): Flow<Result<PostOrderResponse>>

    fun paymentUrl(/*body: OrderIdBody*/orderId: Int): Flow<Result<PaymentUrlResponse>>

    fun getCourse(date: String): Flow<Result<CourseResponseItem>>

}