package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BaseOfferResponse {

    data class Result(
        @field: SerializedName("success")
        val success: Boolean,
        @field: SerializedName("message")
        val message: Boolean,
        @field: SerializedName("error")
        val error: List<Error>,
        @field: SerializedName("data")
        val data: List<DataResult>,
    )

    data class Error(
        @field: SerializedName("id")
        val id: Int,
        @field: SerializedName("message")
        val message: String
    )

    data class DataResult(
        @field: SerializedName("created_at")
        val createdAt: String,
        @field: SerializedName("creator")
        val creator: BaseOffersItemResponse.UserModel,
        @field: SerializedName("description")
        val description: String,
        @field: SerializedName("get_files")
        val getFiles: List<BaseOffersItemResponse.GetFile>,
        @field: SerializedName("id")
        val id: Int,
        @field: SerializedName("spectators")
        val spectators: List<BaseOffersItemResponse.UserModel>,
        @field: SerializedName("to")
        val to: BaseOffersItemResponse.UserModel,
        @field: SerializedName("to_company")
        val toCompany: Boolean,
        @field: SerializedName("status")
        val status: String,
        @field:Expose
        var type: String = "complaint"
    )


}