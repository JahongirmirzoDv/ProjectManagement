package uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating

import java.io.File

/**
 *Created by farrukh_kh on 10/28/21 12:13 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating
 **/
data class CreateDatingRequest(
    val description: String,
    val address: String,
    val startTime: String,
    val endTime: String,
    val files: List<File>?,
    val importance: String,
//    val reminder: Int?,
    val reminders: List<Int>?,
//    val members: List<Int>?,
    val untilDate: String?,
    val repeat: String?,
    val partnerIn: List<Int>?,
    val partnerOut: String?,
    val repeatRule: Int = 0,
)