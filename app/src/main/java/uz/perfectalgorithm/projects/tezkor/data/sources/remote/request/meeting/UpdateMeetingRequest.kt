package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 *Created by farrukh_kh on 8/21/21 9:12 AM
 *uz.rdo.projects.projectmanagement.data.sources.remote.request.meeting
 **/
data class UpdateMeetingRequest(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("address")
    val address: String,
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
    @field:SerializedName("members")
    val newMembers: List<Int>,
    @field:SerializedName("deleted_members")
    val deletedMembers: List<Int>,
//    @field:SerializedName("reminder")
//    val reminder: Int?,
    @field:SerializedName("until_date")
    val reminderDate: String?,
    @field:SerializedName("status")
    val meetingStatus: String?,
    @field:SerializedName("remove_discussed_topics")
    val deletedDiscussedTopics: List<Int>,
    @field:SerializedName("discussed_topics")
    val newDiscussedTopics: List<String>,
    @field:SerializedName("delete_reminders")
    val deletedReminders: List<Int>,
    @field:SerializedName("new_reminders")
    val newReminders: List<Int>,
    val canEditTime: Boolean = false,
    @field:SerializedName("repeat")
    val repeat: String?,
    @field:SerializedName("repeat_rule")
    val repeatRule: Int?,
    @field:SerializedName("description")
    val description: String?,
    val moderators:List<Int> = listOf()
)
