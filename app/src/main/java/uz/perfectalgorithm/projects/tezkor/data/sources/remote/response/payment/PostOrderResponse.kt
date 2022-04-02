package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment

import com.google.gson.annotations.SerializedName

data class PostOrderResponse(

    @field:SerializedName("data")
    val data: OrderData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("errors")
    val errors: List<Any?>? = null
)

data class OrderData(

    @field:SerializedName("amount")
    val amount: Int? = null,

    @field:SerializedName("usd_course")
    val usdCourse: Double? = null,

    @field:SerializedName("order_performed_time")
    val orderPerformedTime: Any? = null,

    @field:SerializedName("amount_usd")
    val amountUsd: Double? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_cancelled_time")
    val orderCancelledTime: Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("order_created_time")
    val orderCreatedTime: Any? = null
)
