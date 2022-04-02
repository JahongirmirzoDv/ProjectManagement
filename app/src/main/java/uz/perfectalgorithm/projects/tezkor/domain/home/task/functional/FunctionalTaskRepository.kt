package uz.perfectalgorithm.projects.tezkor.domain.home.task.functional

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.UpdateTaskRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.folder.CreateFolderRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.*

/**
 * Created by Jasurbek Kurganbaev on 26.06.2021 10:39
 **/
interface FunctionalTaskRepository {

    fun getFunctionalTaskFolderList(): Flow<Result<List<TaskFolderListItem>>>

    suspend fun getFunctionalFolderNew(): DataWrapper<List<TaskFolderListItem>>

    fun getFunctionalList(): Flow<Result<List<FolderItem>>>

    suspend fun getFunctionalListNew(): DataWrapper<List<FolderItem>>

    suspend fun createTaskFolder(createFolderRequest: CreateFolderRequest): DataWrapper<Any>

    suspend fun updateTaskFolder(
        folderId: Int,
        createFolderRequest: CreateFolderRequest
    ): DataWrapper<Any>

    suspend fun deleteTaskFolder(folderId: Int): DataWrapper<Any>

    suspend fun changeTaskStatus(
        taskId: Int,
        changeStatusRequest: ChangeStatusRequest
    ): DataWrapper<Any>

    suspend fun getTaskById(taskId: Int): DataWrapper<TaskDetails>

    suspend fun deleteTask(taskId: Int): DataWrapper<Any>

    suspend fun updateTask(
        taskId: Int,
        updateTaskRequest: UpdateTaskRequest
    ): DataWrapper<Any>

    suspend fun updateTaskEditHistory(taskId: Int,data:HashMap<String,Any?>):DataWrapper<Any>

    suspend fun createSimpleTask(
        folderId: Int,
        statusId: Int,
        title: String
    ): DataWrapper<Any>

    suspend fun getTaskAllComment(id: Int): DataWrapper<List<TaskCommentData>>

    suspend fun postTaskComment(taskCommentRequest: TaskCommentRequest): DataWrapper<Any>
}