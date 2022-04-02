package uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.offers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BaseOffersItemResponse {
    data class Result(
        @field: SerializedName("success")
        val success: Boolean,
        @field: SerializedName("message")
        val message: Boolean,
        @field: SerializedName("error")
        val error: List<Error>,
        @field: SerializedName("data")
        val data: DataResult
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
        val creator: Creator,
        @field: SerializedName("description")
        val description: String,
        @field: SerializedName("get_files")
        val getFiles: List<GetFile>,
        @field: SerializedName("id")
        val id: Int,
        @field: SerializedName("spectators")
        val spectators: List<Spectator>,
        @field: SerializedName("to")
        val to: To,
        @field: SerializedName("to_company")
        val toCompany: Boolean,
        @field: SerializedName("status")
        val status: String,
        @field:Expose
        var type: String = "complaint"
    ) : Serializable


    data class GetFile(
        @field: SerializedName("file")
        val file: String,
        @field: SerializedName("size")
        val size: Long
    )

    data class UserModel(
        @field: SerializedName("first_name")
        val firstName: String,
        @field: SerializedName("last_name")
        val lastName: String
    )

    data class Spectator(
        @field: SerializedName("first_name")
        val firstName: String,
        @field: SerializedName("last_name")
        val lastName: String
    )

    data class To(
        @field: SerializedName("first_name")
        val firstName: String,
        @field: SerializedName("last_name")
        val lastName: String
    )

    data class Creator(
        @field: SerializedName("id")
        val id: Int,
        @field: SerializedName("first_name")
        val firstName: String,
        @field: SerializedName("last_name")
        val lastName: String
    )
}