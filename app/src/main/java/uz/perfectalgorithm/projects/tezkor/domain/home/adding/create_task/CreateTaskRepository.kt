package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.MeetingListItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.*
import java.io.File

/**
 * Created by Jasurbek Kurganbaev on 21.06.2021 17:33
 * Task uchun apilar uchun yaratilingan interface
 **/
interface CreateTaskRepository {

    fun createTask(
        title: String,
        description: String?,
        yaqm: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        project: String?,
        parent: String?,
        folder: Int,
        importance: String,
        participants: List<Int>?,
        spectators: List<Int>?,
        functional_group: List<Int>?,
        repeat: String,
        canEditTime: Boolean,
        reminderDate: String?,
        repeatRule: Int?,
        topicId: Int?,
        planId:Int?,
        isShowCalendar:Boolean
    ): Flow<Result<TaskData>>

    fun sendTask(a: Int): Flow<Result<SendTaskTrue>>

    fun createTaskFromMessage(
        title: String,
        description: String?,
        yaqm: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        project: String?,
        parent: String?,
        folder: Int,
        importance: String,
        participants: List<Int>?,
        spectators: List<Int>?,
        functional_group: List<Int>?,
        repeat: String,
        messageId: Int,
        canEditTime: Boolean,
        reminderDate: String?,
        repeatRule: Int?,
        topicId:Int?,
        planId:Int?,
        isShowCalendar:Boolean
    ): Flow<Result<TaskData>>


    fun getTasks(): Flow<Result<List<FolderItem>>>

    fun getTaskFolder(): Flow<Result<List<TaskFolderListItem>>>

    suspend fun getOwnTaskList(): DataWrapper<List<OwnTaskItem>>


}