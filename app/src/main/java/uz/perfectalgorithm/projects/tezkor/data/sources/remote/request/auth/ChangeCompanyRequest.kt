package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth

import com.google.gson.annotations.SerializedName

data class ChangeCompanyRequest(
    @field:SerializedName("company_id")
    val companyId: Int? = null
)