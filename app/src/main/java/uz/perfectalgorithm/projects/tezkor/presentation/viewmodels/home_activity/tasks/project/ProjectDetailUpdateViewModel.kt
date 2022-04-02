package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.projects.UpdateProjectRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.ProjectDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status.ProjectStatusRepository
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateViewModel

/**
 *Created by farrukh_kh on 8/5/21 11:32 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.project
 **/
class ProjectDetailUpdateViewModel @AssistedInject constructor(
    private val projectRepository: ProjectRepository,
    private val projectStatusRepository: ProjectStatusRepository,
    private val goalMapRepository: GoalMapRepository,
    @Assisted private val projectId: Int
) : ViewModel(), DetailUpdateViewModel {

    private val _project = MutableStateFlow<DataWrapper<ProjectDetails>>(DataWrapper.Empty())
    val project: StateFlow<DataWrapper<ProjectDetails>> get() = _project

//    private val _statusList = MutableStateFlow<DataWrapper<List<StatusData>>>(DataWrapper.Empty())
//    val statusList: StateFlow<DataWrapper<List<StatusData>>> get() = _statusList

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    private val _goalList = MutableStateFlow<DataWrapper<List<GoalData>>>(DataWrapper.Empty())
    val goalList: StateFlow<DataWrapper<List<GoalData>>> get() = _goalList

    init {
        initProject()
//        initStatusList()
        initGoalList()
    }

    fun initProject() = viewModelScope.launch(Dispatchers.IO) {
        _project.value = DataWrapper.Loading()
        _project.value = projectRepository.getProjectById(projectId)
    }

//    override fun initStatusList() = viewModelScope.launch(Dispatchers.IO) {
//        _statusList.value = DataWrapper.Loading()
//        _statusList.value = projectStatusRepository.getStatusList()
//    }

    fun initGoalList() = viewModelScope.launch(Dispatchers.IO) {
        _goalList.value = DataWrapper.Loading()
        _goalList.value = goalMapRepository.getGoalsNew()
    }

    fun updateProject(projectId: Int, updateProjectRequest: UpdateProjectRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value = projectRepository.updateProject(projectId, updateProjectRequest)
        }

    fun clearUpdateResponse() {
        _updateResponse.value = DataWrapper.Empty()
    }

//    override fun initStatusList() =
//        viewModelScope.launch { }

}

@AssistedFactory
interface ProjectDetailUpdateViewModelFactory {
    fun create(projectId: Int): ProjectDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: ProjectDetailUpdateViewModelFactory,
    projectId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(projectId) as T
        }
        throw IllegalArgumentException("$modelClass is not ProjectDetailUpdateViewModel")
    }
}