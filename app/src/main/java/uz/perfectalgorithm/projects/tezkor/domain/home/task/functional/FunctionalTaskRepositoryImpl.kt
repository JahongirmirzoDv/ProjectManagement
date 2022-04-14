package uz.perfectalgorithm.projects.tezkor.domain.home.task.functional

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.TasksApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.UpdateTaskRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder.CreateFolderRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject



class FunctionalTaskRepositoryImpl @Inject constructor(
    private val taskApi: TasksApi,
    private val storage: LocalStorage
) : FunctionalTaskRepository {

    override fun getFunctionalTaskFolderList(): Flow<Result<List<TaskFolderListItem>>> = flow {
        try {
            val response = taskApi.getFunctionalFolderList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else emit(
                Result.failure<List<TaskFolderListItem>>(
                    Exception(
                        App.instance.resources.getString(R.string.error)
                    )
                )
            )
        } catch (e: Exception) {
            timberLog("ChannelRepositoryImpl in function refreshToken error = $e")
            emit(Result.failure<List<TaskFolderListItem>>(e))
        }
    }

    /**
     * [FunctionalTaskRepository] dagi getFunctionalFolderNew() funksiyasining
     * implementatsiyasi.
     */
    override suspend fun getFunctionalFolderNew() = try {
        /**
         * Requestni amalga oshirin, response ni o'zgaruvchiga saqlaymiz.
         */
        val response = taskApi.getFunctionalFolderList()
        /**
         * Agar response muvaffaqiyatli bo'lsa, responsedan o'zimizga kerakli
         * ma'lumotni olib, DataWrapper.Success ga o'rab return qilamiz
         */
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            /**
             * Agar response muvaffaqiytasiz bo'lsa, undan error message ni olib,
             * shu message bilan Exception yaratib, DataWrapper.Error ga o'rab return qilamiz
             */
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        /**
         * Agar shu jarayonlarda nimadir muammo bo'lin exception otsa,
         * uni catch qilib DataWrapper.Error ga o'rab return qilamiz
         */
        DataWrapper.Error(e)
    }

    override fun getFunctionalList(): Flow<Result<List<FolderItem>>> = flow {
        try {
            val response = taskApi.getAllFunctionalList()
            if (response.code() == 200) response.body()?.let {
                emit(Result.success(it))
            } else emit(
                Result.failure<List<FolderItem>>(
                    Exception(
                        App.instance.resources.getString(R.string.error)
                    )
                )
            )
        } catch (e: Exception) {
            timberLog("FunctionalTaskRepository in function refreshToken error = $e", "TTTT")
            emit(Result.failure<List<FolderItem>>(e))
        }
    }

    override suspend fun getFunctionalListNew() = try {
        val response = taskApi.getAllFunctionalList()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun createTaskFolder(createFolderRequest: CreateFolderRequest) = try {
        val response = taskApi.createTaskFolder(createFolderRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateTaskFolder(
        folderId: Int,
        createFolderRequest: CreateFolderRequest
    ) = try {
        val response = taskApi.updateTaskFolder(folderId, createFolderRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteTaskFolder(folderId: Int) = try {
        val response = taskApi.deleteTaskFolder(folderId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun changeTaskStatus(
        taskId: Int,
        changeStatusRequest: ChangeStatusRequest
    ) = try {
        val response = taskApi.changeTaskStatus(taskId, changeStatusRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getTaskById(taskId: Int) = try {
        val response = taskApi.getTaskById(taskId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteTask(taskId: Int) = try {
        val response = taskApi.deleteTask(taskId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateTask(
        taskId: Int,
        updateTaskRequest: UpdateTaskRequest
    ) = try {

        val filesBody = ArrayList<MultipartBody.Part>()

        updateTaskRequest.newFiles?.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

        val participantsBody = updateTaskRequest.participantsId?.size?.let { it1 ->
            Array(it1) {
                updateTaskRequest.participantsId[it].toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        val spectatorBody = updateTaskRequest.observersId?.size?.let { it1 ->
            Array(it1) {
                updateTaskRequest.observersId[it].toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        val functionalBody = updateTaskRequest.functionalGroupId?.size?.let { it1 ->
            Array(it1) {
                updateTaskRequest.functionalGroupId[it].toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        val deletedFilesBody = updateTaskRequest.deletedFiles?.size?.let { it1 ->
            Array(it1) {
                updateTaskRequest.deletedFiles[it].toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        val response = taskApi.updateTask(
            taskId = taskId,
            title = updateTaskRequest.title?.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = updateTaskRequest.description?.toRequestBody("text/plain".toMediaTypeOrNull()),
            yaqm = updateTaskRequest.yaqm?.toRequestBody("text/plain".toMediaTypeOrNull()),
            performer = updateTaskRequest.performerId?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            leader = updateTaskRequest.leaderId?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            start_time = if (updateTaskRequest.startTime.isNullOrBlank()) null else updateTaskRequest.startTime.toRequestBody(
                "text/plain".toMediaTypeOrNull()
            ),
            end_time = if (updateTaskRequest.endTime.isNullOrBlank()) null else updateTaskRequest.endTime.toRequestBody(
                "text/plain".toMediaTypeOrNull()
            ),
            files = filesBody,
            project = updateTaskRequest.project?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            parent = updateTaskRequest.parent?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull()),
            folder = updateTaskRequest.folderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            status = updateTaskRequest.statusId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            importance = updateTaskRequest.importance?.toRequestBody("text/plain".toMediaTypeOrNull()),
            participants = participantsBody,
            spectators = spectatorBody,
            functional_group = functionalBody,
            repeat = updateTaskRequest.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            deletedFiles = deletedFilesBody,
            internalStatus = updateTaskRequest.internalStatus?.toRequestBody("text/plain".toMediaTypeOrNull()),
            comment = updateTaskRequest.comment?.toRequestBody("text/plain".toMediaTypeOrNull()),
            canEditTime = updateTaskRequest.canEditTime.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatRule = updateTaskRequest.repeatRule?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun createSimpleTask(
        folderId: Int,
        statusId: Int,
        title: String
    ) = try {
        val response = taskApi.createSimpleTask(
            folder = folderId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            status = statusId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
        )
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

/*    override suspend fun getAllTaskComment() = try {

        val response = taskApi.getAllTaskComments()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        *//*
    */
    /**
     * Agar shu jarayonlarda nimadir muammo bo'lin exception otsa,
     * uni catch qilib DataWrapper.Error ga o'rab return qilamiz
     *//**//*
        DataWrapper.Error(e)
    }*/




    override suspend fun postTaskComment(taskCommentRequest: TaskCommentRequest) = try {
        val response = taskApi.postTaskComment(taskCommentRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getTaskAllComment(id: Int) = try {
        val response = taskApi.getTaskAllComments(id)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: throw IllegalArgumentException("null"))
        } else {
            DataWrapper.Error(HttpException(response))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateTaskEditHistory(taskId: Int, data: HashMap<String, Any?>): DataWrapper<Any> = try {
        val response = taskApi.updateHistory(taskId, data)
        if (response.isSuccessful) DataWrapper.Success(response.body()?: throw IllegalArgumentException("null"))
        else DataWrapper.Error(HttpException(response))
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

}