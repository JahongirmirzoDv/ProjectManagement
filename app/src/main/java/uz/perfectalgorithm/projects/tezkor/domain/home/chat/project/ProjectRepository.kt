package uz.perfectalgorithm.projects.tezkor.domain.home.chat.project

import kotlinx.coroutines.flow.Flow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.projects.UpdateProjectRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectsByStatus

/**
 * Created by Jasurbek Kurganbaev on 17.06.2021 14:57
 **/
interface ProjectRepository {

    fun getProjects(): Flow<Result<List<ProjectData>>>

    suspend fun getProjectsNew(): DataWrapper<List<ProjectData>>

    suspend fun getProjectsByStatus(): DataWrapper<List<ProjectsByStatus>>

    suspend fun changeProjectStatus(
        projectId: Int,
        changeStatusRequest: ChangeStatusRequest
    ): DataWrapper<Any>

    suspend fun getProjectById(projectId: Int): DataWrapper<ProjectDetails>

    suspend fun deleteProject(projectId: Int): DataWrapper<Any>

    suspend fun updateProject(
        projectId: Int,
        updateProjectRequest: UpdateProjectRequest
    ): DataWrapper<Any>
}