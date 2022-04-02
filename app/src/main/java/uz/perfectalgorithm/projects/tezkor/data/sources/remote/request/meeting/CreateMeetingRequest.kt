package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting

import java.io.File

/**
 *Created by farrukh_kh on 10/27/21 11:59 PM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting
 **/
data class CreateMeetingRequest(
    val title: String,
    var message_id: Int? = null,
    val address: String,
    val startTime: String,
    val endTime: String,
    val files: List<File>?,
    val importance: String,
    val reminders: List<Int>,
//    val reminder: Int?,
    val members: List<Int>,
    val untilDate: String?,
    val repeat: String?,
    val discussedTopics: List<String>,
    val repeatRule: Int = 0,
    val canEditTime: Boolean = false,
    val description: String?,
    val moderators: List<Int> = listOf()
)

data class CreateMeetingComment(
    val meeting: Int,
    val text: String,
    val parent: Int? = null
)