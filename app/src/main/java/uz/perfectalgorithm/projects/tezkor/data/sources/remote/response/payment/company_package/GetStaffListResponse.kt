package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.payment.company_package

import com.google.gson.annotations.SerializedName

data class GetStaffListResponse(

    @field:SerializedName("data")
    val data: List<StaffItem>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class StaffItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("is_active")
    val isActive: Boolean? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null
)
