package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.GoalsApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ProjectApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.CreateProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import java.io.File
import javax.inject.Inject


/**
 * Created by Shahzod Abdurashidov on 22.06.2021 09:56
 **/

class CreateProjectRepositoryImpl @Inject constructor(
    private val goalApi: GoalsApi,
    private val projectApi: ProjectApi,
    private val storage: LocalStorage
) : CreateProjectRepository {


    override fun createProject(
        title: String,
//        description: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        goal: Int,
//        parent: Int?,
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
    ): Flow<Result<CreateProjectData>> = flow {
        try {
            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }

//            val participantsBody = participants?.let { it ->
//                Array(it.size) {
//                    participants[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
//                }
//            }


            val spectatorBody = spectators?.let {
                Array(it.size) {
                    spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val plansBody = Array(plans.size) {
                plans[it].toRequestBody("text/plain".toMediaTypeOrNull())
            }

//            val functionalBody = functional_group?.let {
//                Array(it.size) {
//                    functional_group[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
//                }
//            }

            val response = projectApi.createProject(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
//                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                performer = performer.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                leader = leader.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                start_time = start_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                end_time = end_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
                goal = goal.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                importance = importance.toRequestBody("text/plain".toMediaTypeOrNull()),
//                yaqm = yaqm.toRequestBody("text/plain".toMediaTypeOrNull()),
//                participants = participantsBody,
                spectators = spectatorBody,
//                functional_group = functionalBody,
                canEditTime = canEditTime.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                repeat = repeat.toRequestBody("text/plain".toMediaTypeOrNull()),
                plans = plansBody,
                repeatRule = repeatRule?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                reminderDate = reminderDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
                topic = if (topicId ==0) null else topicId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                plan =if (planId ==0)null else planId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            )

            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                }
            } else {
                emit(Result.failure<CreateProjectData>(HttpException(response)))
            }


        } catch (e: Exception) {
            emit(Result.failure<CreateProjectData>(e))
        }
    }


    override fun getGoals(): Flow<Result<List<GoalData>>> = flow {
        try {
            val response = goalApi.getGoals()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                } else {
                    emit(
                        Result.failure<List<GoalData>>(HttpException(response))
                    )
                }
            }
        } catch (e: Exception) {
            emit(Result.failure<List<GoalData>>(e))
        }
    }

}
