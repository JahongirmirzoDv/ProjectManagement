package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.save_company

import com.google.gson.annotations.SerializedName

sealed class UserDataWithCompanyResponse {
    data class Result(

        @field:SerializedName("data")
        val data: UserWData,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("error")
        val error: List<Any?>? = null
    )

    data class UserWData(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("reports")
        val reports: List<Any?>? = null,

        @field:SerializedName("role")
        val role: String? = null,

        @field:SerializedName("report_period")
        val reportPeriod: String? = null,

        @field:SerializedName("birth_date")
        val birthDate: String? = null,

        @field:SerializedName("sex")
        val sex: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("phone_number")
        val phoneNumber: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("position")
        val position: List<Any?>? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("tasks")
        val tasks: Tasks? = null,

        @field:SerializedName("company_id")
        val companyId: Int? = null,
    )

    data class Tasks(

        @field:SerializedName("all")
        val all: Int? = null,

        @field:SerializedName("done")
        val done: Int? = null,

        @field:SerializedName("undone")
        val undone: Int? = null
    )
}