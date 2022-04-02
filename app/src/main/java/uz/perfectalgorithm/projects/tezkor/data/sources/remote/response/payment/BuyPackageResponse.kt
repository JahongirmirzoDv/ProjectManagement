package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment

import com.google.gson.annotations.SerializedName

data class BuyPackageResponse(

    @field:SerializedName("data")
    val data: PackagePurchaseItem? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: List<Any?>? = null
)

data class PackagePurchaseItem(

    @field:SerializedName("expire_date")
    val expireDate: String? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null
)
