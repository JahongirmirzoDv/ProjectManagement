package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.quick_plan.QuickPlanRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.tactic_plan.TacticPlanRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.dating.DatingRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.meeting.MeetingRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.project_status.ProjectStatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status.TaskStatusRepository
import javax.inject.Inject

/**
 *Created by farrukh_kh on 8/9/21 9:11 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.others
 **/
@HiltViewModel
class DeleteViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: FunctionalTaskRepository,
    private val goalMapRepository: GoalMapRepository,
    private val meetingRepository: MeetingRepository,
    private val datingRepository: DatingRepository,
    private val quickPlanRepository: QuickPlanRepository,
    private val tacticPlanRepository: TacticPlanRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val projectStatusRepository: ProjectStatusRepository
) : ViewModel() {

    private val _response = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val response: StateFlow<DataWrapper<Any>> get() = _response

    fun deleteProject(projectId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = projectRepository.deleteProject(projectId)
        }

    fun deleteTask(taskId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskRepository.deleteTask(taskId)
        }

    fun deleteGoal(goalId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = goalMapRepository.deleteGoal(goalId)
        }

    fun deleteMeeting(meetingId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = meetingRepository.deleteMeeting(meetingId)
        }

    fun deleteDating(datingId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = datingRepository.deleteDating(datingId)
        }

    fun deleteQuickPlan(quickPlanId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = quickPlanRepository.deleteQuickPlan(quickPlanId)
        }

    fun deleteTacticPlan(tacticPlanId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = tacticPlanRepository.deleteTacticPlan(tacticPlanId)
        }

    fun deleteTaskStatus(statusId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskStatusRepository.deleteStatus(statusId)
        }

    fun deleteProjectStatus(statusId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = projectStatusRepository.deleteStatus(statusId)
        }

    fun deleteTaskFolder(folderId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = DataWrapper.Loading()
            _response.value = taskRepository.deleteTaskFolder(folderId)
        }

    fun clearResponse() {
        _response.value = DataWrapper.Empty()
    }
}