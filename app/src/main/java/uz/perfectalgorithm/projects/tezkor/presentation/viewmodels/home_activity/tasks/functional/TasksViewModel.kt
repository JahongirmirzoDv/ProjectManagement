package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.status.ChangeStatusRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * by farrukh_kh
 **/
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: FunctionalTaskRepository
) : ViewModel() {

    private val _folders = MutableStateFlowWrapper<List<TaskFolderListItem>>()
    val folders: StateFlowWrapper<List<TaskFolderListItem>> get() = _folders.asStateFlow()

    private val _tasks = MutableStateFlowWrapper<List<FolderItem>>()
    val tasks: StateFlowWrapper<List<FolderItem>> get() = _tasks.asStateFlow()

    private val _changeStatusResponse = MutableStateFlowWrapper<Any>()
    val changeStatusResponse: StateFlowWrapper<Any> get() = _changeStatusResponse.asStateFlow()

    private val _createResponse = MutableStateFlowWrapper<Any>()
    val createResponse: StateFlowWrapper<Any> get() = _createResponse.asStateFlow()

    init {
        initFolders()
    }

    fun initFolders() = launchCoroutine {
        _folders.value = DataWrapper.Loading()
        _folders.value = taskRepository.getFunctionalFolderNew()
    }

    fun initTasks() = launchCoroutine {
        _tasks.value = DataWrapper.Loading()
        _tasks.value = taskRepository.getFunctionalListNew()
    }

    fun changeStatus(taskId: Int, changeStatusRequest: ChangeStatusRequest) = launchCoroutine {
        _changeStatusResponse.value = DataWrapper.Loading()
        _changeStatusResponse.value = taskRepository.changeTaskStatus(taskId, changeStatusRequest)
    }

    fun createTask(title: String, folderId: Int, statusId: Int) = launchCoroutine {
        _createResponse.value = DataWrapper.Loading()
        _createResponse.value = taskRepository.createSimpleTask(folderId, statusId, title)
    }
}