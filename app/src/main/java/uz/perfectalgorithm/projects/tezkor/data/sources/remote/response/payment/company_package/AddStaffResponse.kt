package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

data class AddStaffResponse(

    @field:SerializedName("data")
    val data: AddStaffData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class AddStaffData(

    @field:SerializedName("cost")
    val cost: Double? = null,

    @field:SerializedName("price_per_user")
    val pricePerUser: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("lost_days")
    val lostDays: Int? = null
)
