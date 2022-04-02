package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 *Created by farrukh_kh on 8/21/21 9:12 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.dating
 **/
data class UpdateDatingRequest(
//    @field:SerializedName("title")
//    val title: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("partner_ins")
    val partnerIn: List<Int>?,
    @field:SerializedName("partner_out")
    val partnerOut: String?,
    @field:SerializedName("start_time")
    val startTime: String,
    @field:SerializedName("end_time")
    val endTime: String,
    @field:SerializedName("deleted_files")
    val deletedFiles: List<Int>,
    @field:SerializedName("files")
    val newFiles: List<File>,
    @field:SerializedName("importance")
    val importance: String,
//    @field:SerializedName("members")
//    val participantsId: List<Int>,
//    @field:SerializedName("reminder")
//    val reminder: Int?,
    @field:SerializedName("until_date")
    val reminderDate: String?,
    @field:SerializedName("delete_reminders")
    val deletedReminders: List<Int>,
    @field:SerializedName("new_reminders")
    val newReminders: List<Int>,
    @field:SerializedName("repeat")
    val repeat: String?,
    @field:SerializedName("repeat_rule")
    val repeatRule: Int?,
)