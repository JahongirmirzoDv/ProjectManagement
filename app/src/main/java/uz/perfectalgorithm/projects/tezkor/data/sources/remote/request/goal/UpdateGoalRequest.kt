package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.goal

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 *Created by farrukh_kh on 8/21/21 10:17 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.goal
 **/
data class UpdateGoalRequest(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("performer")
    val performerId: Int,
    @field:SerializedName("leader")
    val leaderId: Int,
    @field:SerializedName("start_time")
    val startTime: String,
    @field:SerializedName("end_time")
    val endTime: String,
    @field:SerializedName("deleted_files")
    val deletedFiles: List<Int>,
    @field:SerializedName("files")
    val newFiles: List<File>,
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("folder")
    val folderId: Int,
    @field:SerializedName("participants")
    val participantsId: List<Int>,
    @field:SerializedName("spectators")
    val observersId: List<Int>,
    @field:SerializedName("functional_group")
    val functionalGroupId: List<Int>,
    @field:SerializedName("is_editable")
    val canEditTime: Boolean
)