package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

data class GetPackageListResponse(

    @field:SerializedName("data")
    val data: List<PackageItemData>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class PackageItemData(

    @field:SerializedName("next")
    val next: Int? = null,

    @field:SerializedName("expire_date")
    val expireDate: String? = null,

    @field:SerializedName("price")
    val price: Double? = null,

    @field:SerializedName("staff_limit")
    val staffLimit: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("lost_days")
    val lostDays: Int? = null,

    @field:SerializedName("is_demo")
    val isDemo: Boolean? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null
)
