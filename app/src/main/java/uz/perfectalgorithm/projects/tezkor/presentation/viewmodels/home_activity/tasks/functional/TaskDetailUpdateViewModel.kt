package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.functional

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
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.TaskCommentRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.tasks.UpdateTaskRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskCommentPagingData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskDetails
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.functional.FunctionalTaskRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.task.task_status.TaskStatusRepository
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.detail_update.DetailUpdateViewModel

/**
 *Created by farrukh_kh on 8/6/21 10:01 AM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks.functional
 **/
class TaskDetailUpdateViewModel @AssistedInject constructor(
    private val taskRepository: FunctionalTaskRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val repetitionRepository: RepetitionRepository,
    private val projectRepository: ProjectRepository,
    @Assisted private val taskId: Int
) : ViewModel(), DetailUpdateViewModel {

    private val _task = MutableStateFlow<DataWrapper<TaskDetails>>(DataWrapper.Empty())
    val task: StateFlow<DataWrapper<TaskDetails>> get() = _task

    private val _updateResponse = MutableStateFlow<DataWrapper<Any>>(DataWrapper.Empty())
    val updateResponse: StateFlow<DataWrapper<Any>> get() = _updateResponse

    var isShowCalendar: Boolean = true

//    private val _statusList = MutableStateFlow<DataWrapper<List<StatusData>>>(DataWrapper.Empty())
//    val statusList: StateFlow<DataWrapper<List<StatusData>>> get() = _statusList

    private val _taskComments =
        MutableStateFlow<DataWrapper<List<TaskCommentData>>>(DataWrapper.Empty())
    val taskComments: StateFlow<DataWrapper<List<TaskCommentData>>> get() = _taskComments

    private val _folders =
        MutableStateFlow<DataWrapper<List<TaskFolderListItem>>>(DataWrapper.Empty())
    val folders: StateFlow<DataWrapper<List<TaskFolderListItem>>> get() = _folders

    /*   private val _repeats = MutableStateFlow<DataWrapper<List<RepetitionData>>>(DataWrapper.Empty())
       val repeats: StateFlow<DataWrapper<List<RepetitionData>>> get() = _repeats
   */

    private val _projects = MutableStateFlow<DataWrapper<List<ProjectData>>>(DataWrapper.Empty())
    val projects: StateFlow<DataWrapper<List<ProjectData>>> get() = _projects


    /*  private val _tasks = MutableStateFlow<DataWrapper<List<FolderItem>>>(DataWrapper.Empty())
      val tasks: StateFlow<DataWrapper<List<FolderItem>>> get() = _tasks
  */

    /*  private val _comments =
          MutableStateFlow<DataWrapper<List<TaskCommentResponseData>>>(DataWrapper.Empty())
      val comments: StateFlow<DataWrapper<List<TaskCommentResponseData>>> get() = _comments
  */
    init {
        initTask()
//        initStatusList()
        initFolders()
//        initRepeats()
        initProjects()
//        initTasks()
        initTaskComments()
    }

    fun initTask() = viewModelScope.launch(Dispatchers.IO) {
        _task.value = DataWrapper.Loading()
        _task.value = taskRepository.getTaskById(taskId)

    }

//    override fun initStatusList() = viewModelScope.launch(Dispatchers.IO) {
//        _statusList.value = DataWrapper.Loading()
//        _statusList.value = taskStatusRepository.getStatusList()
//    }

    fun initFolders() = viewModelScope.launch(Dispatchers.IO) {
        _folders.value = DataWrapper.Loading()
        _folders.value = taskRepository.getFunctionalFolderNew()
    }


/*    fun initRepeats() = viewModelScope.launch(Dispatchers.IO) {
        _repeats.value = DataWrapper.Loading()
        _repeats.value = repetitionRepository.getRepetitionsNew()
    }*/

    fun initProjects() = viewModelScope.launch(Dispatchers.IO) {
        _projects.value = DataWrapper.Loading()
        _projects.value = projectRepository.getProjectsNew()
    }

/*    fun initTasks() = viewModelScope.launch(Dispatchers.IO) {
        _tasks.value = DataWrapper.Loading()
        _tasks.value = taskRepository.getFunctionalListNew()
    }*/

    fun initTaskComments() = viewModelScope.launch(Dispatchers.IO) {
        _taskComments.value = DataWrapper.Loading()
        _taskComments.value = taskRepository.getTaskAllComment(taskId)
    }

    fun updateTask(updateTaskRequest: UpdateTaskRequest) = viewModelScope.launch(Dispatchers.IO) {
        val oldTaskData: TaskDetails? = when (task.value) {
            is DataWrapper.Success<TaskDetails> -> {
                (task.value as DataWrapper.Success<TaskDetails>).data
            }
            else -> null
        }
        val changer = getChangedValue(oldTaskData,updateTaskRequest)
        taskRepository.updateTaskEditHistory(taskId,changer)
        _updateResponse.value = DataWrapper.Loading()
        _updateResponse.value = taskRepository.updateTask(taskId, updateTaskRequest)
    }

    private fun getChangedValue(old: TaskDetails?, new: UpdateTaskRequest) :HashMap<String,Any?>{
        val hash = HashMap<String, Any?>()
        if (old?.title != new.title) {
            hash["title"] = new.title
        }
        if (old?.description != new.description) {
            hash["description"] = new.description
        }
        if (old?.yaqm != new.yaqm) {
            hash["yaqm"] = new.yaqm
        }
        if (old?.internalStatus != new.internalStatus) {
            hash["internal_status"] = new.internalStatus
        }
        if (new.performerId != old?.performer?.id) {
            hash["performer"] = new.performerId
        }
        if (new.leaderId != old?.leader?.id) {
            hash["leader"] = new.leaderId
        }
        if (new.startTime != old?.startTime) {
            hash["start_time"] = new.startTime
        }
        if (new.endTime != old?.endTime) {
            hash["end_time"] = new.endTime
        }
        if (!new.deletedFiles.isNullOrEmpty()) {
            hash["deleted_files"] = new.deletedFiles
        }
        if (new.newFiles != old?.files) {
            hash["files"] = new.newFiles
        }
        if (new.statusId != old?.status?.id) {
            hash["status"] = new.statusId
        }
        if (new.importance != old?.importance) {
            hash["importance"] = new.importance
        }
        if (new.parent != old?.parent?.id) {
            hash["parent"] = new.parent
        }
        if (new.folderId != old?.folder?.id) {
            hash["fold?er"] = new.folderId
        }
        if (new.participantsId != old?.participants?.map { it.id }) {
            hash["participants"] = new.participantsId
        }
        if (new.observersId != old?.spectators?.map { it.id }) {
            hash["spectators"] = new.observersId
        }
        if (new.functionalGroupId != old?.functionalGroup?.map { it.id }) {
            hash["functional_group"] = new.functionalGroupId
        }
        if (new.internalStatus != old?.internalStatus) {
            hash["internal_status"] = new.internalStatus
        }
        if (new.canEditTime != old?.canEditTime) {
            hash["is_editable"] = new.canEditTime
        }
        if (new.repeat != old?.repeat) {
            hash["repeat"] = new.repeat
        }
        if (new.repeatRule != old?.repeatRule) {
            hash["repeat_rule"] = new.repeatRule
        }
        if (new.isShowCalendar != old?.isShowCalendar) {
            hash["is_calendar"] = new.isShowCalendar
        }
        Timber.tag("chech_edit").d(hash.toString())
        return hash
    }

    fun postComment(taskCommentRequest: TaskCommentRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _updateResponse.value = DataWrapper.Loading()
            _updateResponse.value = taskRepository.postTaskComment(taskCommentRequest)
        }

    fun clearUpdateResponse() {
        _updateResponse.value = DataWrapper.Empty()
    }
}

@AssistedFactory
interface TaskDetailUpdateViewModelFactory {
    fun create(taskId: Int): TaskDetailUpdateViewModel
}

fun provideFactory(
    viewModelFactory: TaskDetailUpdateViewModelFactory,
    taskId: Int
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailUpdateViewModel::class.java)) {
            return viewModelFactory.create(taskId) as T
        }
        throw IllegalArgumentException("$modelClass is not TaskDetailUpdateViewModel")
    }
}