package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.ProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.FolderItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.SendTaskTrue
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.TaskFolderListItem
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status.StatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_task.CreateTaskRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.repetition.RepetitionRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.project.ProjectRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 19.06.2021 17:40
 * Task uchun yaratilingan ViewModel unda repository ga murojaat qilingan
 **/

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskRepository: CreateTaskRepository,
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    var title: String? = null
    var topicId: Int? = null
    var planId: Int? = null
    var isShowCalendar: Boolean = true
    var description: String? = ""
    var yaQM: String? = null
    var performer: Int? = null
    var leader: Int? = null
    var startDate: String? = null
    var startTime: String? = null
    var endDate: String? = null
    var endTime: String? = null

    //    var files: ArrayList<File> = ArrayList()
    var status: StatusData? = null

    var importance: String? = ""
    var participants: List<Int>? = null
    var spectators: List<Int>? = null
    var functionalGroups: List<Int>? = null
    var canEditTime: Boolean? = null

    //    var repitation: String? = null
    var project: String? = null
    var parent: String? = ""
    var folder: Int? = null

    var repeat: String? = null
    var repeatWeekRule = 0
    var repeatMonthRule = 0
    var reminderDate: LocalDateTime? = null

    var importanceText: String? = null

    var fromMessageId: Int? = null

    var taskList: ArrayList<FolderItem>? = null
    var projectList: ArrayList<ProjectData>? = null
    var statusList: ArrayList<StatusData>? = null
    var folderList: ArrayList<TaskFolderListItem>? = null
    var repetitionList: ArrayList<RepetitionData>? = null


    private val _createTaskLiveData = MutableLiveData<TaskData>()
    val createTaskLiveData: LiveData<TaskData> get() = _createTaskLiveData

    private val _makeTaskLiveData = MutableLiveData<SendTaskTrue>()
    val makeTaskLiveData: LiveData<SendTaskTrue> get() = _makeTaskLiveData

/*
    private val _allTaskLiveData = MutableLiveData<List<FolderItem>>()
    val allTaskLiveData: LiveData<List<FolderItem>> get() = _allTaskLiveData
*/

    private val _allTaskFolderLiveData = MutableLiveData<List<TaskFolderListItem>>()
    val allTaskFolderLiveData: LiveData<List<TaskFolderListItem>> get() = _allTaskFolderLiveData

    private val _allProjectLiveData = MutableLiveData<List<ProjectData>>()
    val allProjectLiveData: LiveData<List<ProjectData>> get() = _allProjectLiveData


    /*   private val _allRepetitionLiveData = MutableLiveData<List<RepetitionData>>()
       val allRepetitionLiveData: LiveData<List<RepetitionData>> get() = _allRepetitionLiveData
   */
    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    init {

/*        if (repetitionList == null) {
            getRepetitions()
        }*/

        if (projectList == null) {
            getProjects()
        }
/*        if (taskList == null) {
            getTasks()
        }*/
        if (folderList == null) {
            getTaskFunctionalFolder()
        }

    }

    private fun getTaskFunctionalFolder() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            createTaskRepository.getTaskFolder().collect {
                _progressLiveData.postValue(false)
                it.onSuccess { taskFolderList ->
                    _allTaskFolderLiveData.postValue(taskFolderList)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

/*    private fun getRepetitions() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repetitionRepository.getRepetitions().collect {
                _progressLiveData.postValue(false)
                it.onSuccess { repetitionList ->
                    _allRepetitionLiveData.postValue(repetitionList)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }*/


/*    fun getTasks() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            createTaskRepository.getTasks().collect {
                _progressLiveData.postValue(false)
                it.onSuccess { goalMapData ->
                    _allTaskLiveData.postValue(goalMapData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)

                }
            }
        }
    }*/

    fun getProjects() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProjects().collect {
                _progressLiveData.postValue(false)
                it.onSuccess { goalMapData ->
                    _allProjectLiveData.postValue(goalMapData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _progressLiveData.postValue(false)
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun sendToQuickTask(b: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            createTaskRepository.sendTask(b).collect {
                it.onSuccess { taskData ->
                    _makeTaskLiveData.postValue(taskData)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun createTask(
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
    ) {
        _progressLiveData.value = true
        if (fromMessageId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                createTaskRepository.createTaskFromMessage(
                    title = title,
                    description = description,
                    yaqm = yaqm,
                    performer = performer,
                    leader = leader,
                    start_time = start_time,
                    end_time = end_time,
                    files = files,
                    project = project,
                    parent = parent,
                    folder = folder,
                    importance = importance,
                    participants = participants,
                    spectators = spectators,
                    functional_group = functional_group,
                    repeat = repeat,
                    messageId = fromMessageId!!,
                    canEditTime = canEditTime,
                    reminderDate,
                    repeatRule,
                    topicId,
                    planId,
                    isShowCalendar
                ).collect {
                    it.onSuccess { taskData ->
                        _createTaskLiveData.postValue(taskData)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)
                    }
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                createTaskRepository.createTask(
                    title = title,
                    description = description,
                    yaqm = yaqm,
                    performer = performer,
                    leader = leader,
                    start_time = start_time,
                    end_time = end_time,
                    files = files,
                    project = project,
                    parent = parent,
                    folder = folder,
                    importance = importance,
                    participants = participants,
                    spectators = spectators,
                    functional_group = functional_group,
                    repeat = repeat,
                    canEditTime = canEditTime,
                    reminderDate,
                    repeatRule,
                    topicId,
                    planId,
                    isShowCalendar
                ).collect {
                    it.onSuccess { goalData ->
                        _createTaskLiveData.postValue(goalData)
                        _progressLiveData.postValue(false)
                    }
                    it.onFailure { throwable ->
                        _errorLiveData.postValue(throwable)
                        _progressLiveData.postValue(false)

                    }
                }
            }
        }
    }
}

