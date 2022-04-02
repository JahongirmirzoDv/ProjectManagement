package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.CreateProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import java.io.File

/**
 * Created by Shahzod Abdurashidov on 22.06.2021 09:57
 **/

interface CreateProjectRepository {

    fun createProject(
        title: String,
//        description: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        folder: Int,
        importance: String,
//        yaqm: String,
//        participants: List<Int>?,
        spectators: List<Int>?,
//        functional_group: List<Int>?,
        canEditTime: Boolean,
        repeat: String,
        plans: List<String>,
        reminderDate: String?,
        repeatRule: Int?,
        topicId:Int?,
        planId:Int?
    ): Flow<Result<CreateProjectData>>

    fun getGoals(): Flow<Result<List<GoalData>>>

}
