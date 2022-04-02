package uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.TasksApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.*
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 21.06.2021 17:34
 **/

class CreateTaskRepositoryImpl @Inject constructor(
    private val createTaskApi: TasksApi,
) : CreateTaskRepository {

    override fun createTask(
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
        topicId:Int?,
        planId:Int?,
        isShowCalendar:Boolean
    ): Flow<Result<TaskData>> = flow {
        try {
            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }

            val participantsBody = participants?.let { it ->
                Array(it.size) {
                    participants[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }


            val spectatorBody = spectators?.let {
                Array(it.size) {
                    spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val functionalBody = functional_group?.let {
                Array(it.size) {
                    functional_group[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val response = createTaskApi.createTask(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                yaqm = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                performer = performer.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                leader = leader.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                start_time = start_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                end_time = end_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
                project = project?.toRequestBody("text/plain".toMediaTypeOrNull()),
                parent = parent?.toRequestBody("text/plain".toMediaTypeOrNull()),
                folder = folder.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                importance = importance.toRequestBody("text/plain".toMediaTypeOrNull()),
                participants = participantsBody,
                spectators = spectatorBody,
                functional_group = functionalBody,
                repeat = repeat.toRequestBody("text/plain".toMediaTypeOrNull()),
                repeatRule = repeatRule?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                repeatExceptions = listOf<RequestBody>(),
                canEditTime = canEditTime.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                reminderDate = reminderDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
                topic =if (topicId ==0)null else topicId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                plan = if (planId == 0) null else planId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                isShowCalendar = isShowCalendar.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )

            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                }
            } else {
                emit(Result.failure<TaskData>(HttpException(response)))
            }


        } catch (e: Exception) {
            emit(Result.failure<TaskData>(e))
        }
    }

    override fun sendTask(a: Int): Flow<Result<SendTaskTrue>> = flow {
        try {
            val sendTaskTrue = createTaskApi.sendTaskTrue(a)

            if (sendTaskTrue.code() == 201) sendTaskTrue.body().let {
                emit(Result.success(it?: SendTaskTrue("", -1,false, Any(), false, "")))
            } else {
                emit(Result.failure<SendTaskTrue>(HttpException(sendTaskTrue)))
            }
        } catch (e: Exception) {
            emit(Result.failure<SendTaskTrue>(e))
        }
    }

    override fun createTaskFromMessage(
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
        topicId: Int?,
        planId:Int?,
        isShowCalendar:Boolean
    ): Flow<Result<TaskData>> = flow {
        try {
            val filesBody: ArrayList<MultipartBody.Part> = ArrayList()
            if (files != null) {
                for (i in files.indices) {
                    filesBody.add(prepareImageFilePart("files", files[i]))
                }
            }

            val participantsBody = participants?.let { it ->
                Array(it.size) {
                    participants[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }


            val spectatorBody = spectators?.let {
                Array(it.size) {
                    spectators[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val functionalBody = functional_group?.let {
                Array(it.size) {
                    functional_group[it].toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }

            val response = createTaskApi.createTaskFromMessage(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                yaqm = description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                performer = performer.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                leader = leader.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                start_time = start_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                end_time = end_time.toRequestBody("text/plain".toMediaTypeOrNull()),
                files = filesBody,
                project = project?.toRequestBody("text/plain".toMediaTypeOrNull()),
                parent = parent?.toRequestBody("text/plain".toMediaTypeOrNull()),
                folder = folder.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                importance = importance.toRequestBody("text/plain".toMediaTypeOrNull()),
                participants = participantsBody,
                spectators = spectatorBody,
                functional_group = functionalBody,
                repeat = repeat.toRequestBody("text/plain".toMediaTypeOrNull()),
                messageId = messageId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                canEditTime = canEditTime.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                repeatRule = repeatRule?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                reminderDate = reminderDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
                topic =if (topicId ==0)null else topicId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                plan =if (planId == 0)null else planId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                isShowCalendar = isShowCalendar.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )

            if (response.code() == 201) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                }
            } else {
                emit(Result.failure<TaskData>(HttpException(response)))
            }


        } catch (e: Exception) {
            emit(Result.failure<TaskData>(e))
        }
    }

    override fun getTasks(): Flow<Result<List<FolderItem>>> = flow {
        try {
            val response = createTaskApi.getAllFunctionalList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(Result.failure<List<FolderItem>>(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<FolderItem>>(e))
        }
    }


    override suspend fun getOwnTaskList(): DataWrapper<List<OwnTaskItem>> = try {
        val response = createTaskApi.getOwnTaskList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }


    override fun getTaskFolder(): Flow<Result<List<TaskFolderListItem>>> = flow {
        try {
            val response = createTaskApi.getFunctionalFolderList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else {
                emit(Result.failure<List<TaskFolderListItem>>(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.failure<List<TaskFolderListItem>>(e))
        }
    }


}