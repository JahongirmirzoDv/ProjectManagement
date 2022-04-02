package uz.perfectalgorithm.projects.tezkor.domain.home.chat.project

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.apis.ProjectApi
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.projects.UpdateProjectRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.utils.prepareImageFilePart
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 14:58
 **/
class ProjectRepositoryImpl @Inject constructor(
    private val projectApi: ProjectApi,
    private val storage: LocalStorage
) : ProjectRepository {

    override fun getProjects(): Flow<Result<List<ProjectData>>> = flow {
        try {
            val response = projectApi.getProjects()
            if (response.code() == 200) response.body()?.let {
                if (it.data != null) {
                    emit(Result.success(it.data))
                }
            } else emit(
                Result.failure<List<ProjectData>>(
                    HttpException(response)
                )
            )
        } catch (e: Exception) {
            timberLog("ProjectRepositoryImpl in function refreshToken error = $e")
            emit(Result.failure<List<ProjectData>>(e))
        }
    }

    override suspend fun getProjectsNew() = try {
        val response = projectApi.getProjects()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()?.data ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getProjectsByStatus() = try {
        val response = projectApi.getProjectsByStatus()
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: emptyList())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun changeProjectStatus(
        projectId: Int,
        changeStatusRequest: ChangeStatusRequest
    ) = try {
        val response = projectApi.changeProjectStatus(projectId, changeStatusRequest)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun getProjectById(projectId: Int) = try {
        val response = projectApi.getProjectById(projectId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body()!!)
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun deleteProject(projectId: Int) = try {
        val response = projectApi.deleteProject(projectId)
        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }

    override suspend fun updateProject(
        projectId: Int,
        updateProjectRequest: UpdateProjectRequest
    ) = try {

        val filesBody = ArrayList<MultipartBody.Part>()
        updateProjectRequest.newFiles.forEach { file ->
            filesBody.add(prepareImageFilePart("files", file))
        }

//        val participantsBody = Array(updateProjectRequest.participantsId.size) {
//            updateProjectRequest.participantsId[it].toString()
//                .toRequestBody("text/plain".toMediaTypeOrNull())
//        }

        val spectatorBody = Array(updateProjectRequest.observersId.size) {
            updateProjectRequest.observersId[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

//        val functionalBody = Array(updateProjectRequest.functionalGroupId.size) {
//            updateProjectRequest.functionalGroupId[it].toString()
//                .toRequestBody("text/plain".toMediaTypeOrNull())
//        }

        val deletedFilesBody = Array(updateProjectRequest.deletedFiles.size) {
            updateProjectRequest.deletedFiles[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val newPlansBody = Array(updateProjectRequest.newPlans.size) {
            updateProjectRequest.newPlans[it]
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val deletedPlansBody = Array(updateProjectRequest.deletedPlans.size) {
            updateProjectRequest.deletedPlans[it].toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val response = projectApi.updateProject(
            projectId = projectId,
            title = updateProjectRequest.title.toRequestBody("text/plain".toMediaTypeOrNull()),
//            description = updateProjectRequest.description.toRequestBody("text/plain".toMediaTypeOrNull()),
//            yaqm = updateProjectRequest.yaqm.toRequestBody("text/plain".toMediaTypeOrNull()),
            performer = updateProjectRequest.performerId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            leader = updateProjectRequest.leaderId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            start_time = updateProjectRequest.startTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            end_time = updateProjectRequest.endTime.toRequestBody("text/plain".toMediaTypeOrNull()),
            files = filesBody,
            goal = updateProjectRequest.goalId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            status = updateProjectRequest.statusId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            importance = updateProjectRequest.importance.toRequestBody("text/plain".toMediaTypeOrNull()),
//            participants = participantsBody,
            spectators = spectatorBody,
//            functional_group = functionalBody,
            deletedFiles = deletedFilesBody,
            internalStatus = updateProjectRequest.internalStatus?.toRequestBody("text/plain".toMediaTypeOrNull()),
            comment = updateProjectRequest.comment?.toRequestBody("text/plain".toMediaTypeOrNull()),
            canEditTime = updateProjectRequest.canEditTime.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull()),
            newPlans = newPlansBody,
            deletedPlans = deletedPlansBody,
            repeat = updateProjectRequest.repeat?.toRequestBody("text/plain".toMediaTypeOrNull()),
            repeatRule = updateProjectRequest.repeatRule?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        if (response.isSuccessful) {
            DataWrapper.Success(response.body() ?: Any())
        } else {
            DataWrapper.Error(Exception(response.message()))
        }
    } catch (e: Exception) {
        DataWrapper.Error(e)
    }
}