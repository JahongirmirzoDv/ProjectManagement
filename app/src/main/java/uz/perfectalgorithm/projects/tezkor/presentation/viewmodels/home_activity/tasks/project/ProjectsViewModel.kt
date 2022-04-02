package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectsByStatus
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * by farrukh_kh
 **/
@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _projects = MutableStateFlowWrapper<List<ProjectsByStatus>>()
    val projects: StateFlowWrapper<List<ProjectsByStatus>> get() = _projects.asStateFlow()

    private val _changeStatusResponse = MutableStateFlowWrapper<Any>()
    val changeStatusResponse: StateFlowWrapper<Any> get() = _changeStatusResponse.asStateFlow()

    init {
        initProjects()
    }

    fun initProjects() = launchCoroutine {
        _projects.value = DataWrapper.Loading()
        _projects.value = projectRepository.getProjectsByStatus()
    }

    fun changeProjectStatus(projectId: Int, changeStatusRequest: ChangeStatusRequest) =
        launchCoroutine {
            _changeStatusResponse.value = DataWrapper.Loading()
            _changeStatusResponse.value =
                projectRepository.changeProjectStatus(projectId, changeStatusRequest)
        }
}