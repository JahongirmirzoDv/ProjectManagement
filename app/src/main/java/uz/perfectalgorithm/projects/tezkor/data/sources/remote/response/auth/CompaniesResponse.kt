package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth

import com.google.gson.annotations.SerializedName

sealed class CompaniesResponse {
    data class Result(

        @field:SerializedName("data")
        val data: List<CompanyData>,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class CompanyData(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("main_target")
        val mainTarget: String? = null,

        @field:SerializedName("bank_account_number")
        val bankAccountNumber: String? = null,

        @field:SerializedName("is_active")
        val isActive: Boolean? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("zip_code")
        val zipCode: String? = null,

        @field:SerializedName("legal_address")
        val legalAddress: String? = null,

        @field:SerializedName("mfo")
        val mfo: String? = null,

        @field:SerializedName("bank_name")
        val bankName: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("stir")
        val stir: String? = null,

        @field:SerializedName("email")
        val email: String? = null
    ) {
        constructor() : this("")
    }
}