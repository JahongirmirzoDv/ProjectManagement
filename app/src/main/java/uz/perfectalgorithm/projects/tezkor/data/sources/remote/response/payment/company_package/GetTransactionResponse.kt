package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

data class GetTransactionResponse(

    @field:SerializedName("data")
    val data: TransactionData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class TransactionData(

    @field:SerializedName("data_list")
    val dataList: List<TransactionListItem>? = null,

    @field:SerializedName("company_balance")
    val companyBalance: Double? = null
)

data class TransactionListItem(

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("is_purchase")
    val isPurchase: Boolean? = null
)
