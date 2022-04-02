package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.projects

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 *Created by farrukh_kh on 8/16/21 12:05 PM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.projects
 **/
data class UpdateProjectRequest(
    @field:SerializedName("title")
    val title: String,
//    @field:SerializedName("description")
//    val description: String,
//    @field:SerializedName("yaqm")
//    val yaqm: String,
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
    val statusId: Int,
    @field:SerializedName("importance")
    val importance: String,
    @field:SerializedName("folder")
    val goalId: Int,
//    @field:SerializedName("participants")
//    val participantsId: List<Int>,
    @field:SerializedName("spectators")
    val observersId: List<Int>,
//    @field:SerializedName("functional_group")
//    val functionalGroupId: List<Int>,
    @field:SerializedName("internal_status")
    val internalStatus: String? = "",
    @field:SerializedName("comment")
    val comment: String? = "",
    @field:SerializedName("is_editable")
    val canEditTime: Boolean,
    @field:SerializedName("remove_project_plans")
    val deletedPlans: List<Int>,
    @field:SerializedName("project_plans")
    val newPlans: List<String>,
    @field:SerializedName("repeat")
    val repeat: String?,
    @field:SerializedName("repeat_rule")
    val repeatRule: Int?,
)