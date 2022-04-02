package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

data class AddDateResponse(

    @field:SerializedName("data")
    val data: AddDateData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class AddDateData(

    @field:SerializedName("date")
    val date: Int? = null,

    @field:SerializedName("cost")
    val cost: Int? = null,

    @field:SerializedName("price_per_user")
    val pricePerUser: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null
)
