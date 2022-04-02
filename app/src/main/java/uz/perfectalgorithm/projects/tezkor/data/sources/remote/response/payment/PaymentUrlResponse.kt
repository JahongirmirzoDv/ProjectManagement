package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment

import com.google.gson.annotations.SerializedName

data class PaymentUrlResponse(

    @field:SerializedName("data")
    val data: UrlData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("errors")
    val errors: List<Any?>? = null
)

data class UrlData(

    @field:SerializedName("url")
    val url: String? = null
)
