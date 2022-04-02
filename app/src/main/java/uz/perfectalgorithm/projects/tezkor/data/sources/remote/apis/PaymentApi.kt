package uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis

import retrofit2.Response
import retrofit2.http.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.*

/**
 * Created by Jasurbek Kurganbaev on 8/19/2021 2:43 PM
 **/
interface PaymentApi {

    @Multipart
    @POST("/api/v1/paycom/order-create/")
    suspend fun postOrder(@Part("amount") amount: Int): Response<PostOrderResponse>

    @Multipart
    @POST("/api/v1/paycom/generate-payment-url/")
    suspend fun paymentUrl(@Part("order_id") order_id: Int): Response<PaymentUrlResponse>


}