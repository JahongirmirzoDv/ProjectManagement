package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetTariffPlansListResponse(

    @field:SerializedName("data")
    val data: TariffData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class TariffData(

    @field:SerializedName("data_list")
    val dataList: List<TariffListItem?>? = null,

    @field:SerializedName("company_balance")
    val companyBalance: Double? = null
)

data class TariffListItem(

    @field:SerializedName("user_price_per_month")
    val userPricePerMonth: Int? = null,

    @field:SerializedName("min_staffs")
    val minStaffs: Int? = null,

    @field:SerializedName("company_price_per_month")
    val companyPricePerMonth: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_pinned")
    val isPinned: Boolean? = null,

    @Expose
    var isChanged: Boolean = false,
)
