package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.chat.project.CreateProjectData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.CreateProjectRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.adding.create_project.status.StatusRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 19.06.2021 17:45
 **/

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val createProjectRepository: CreateProjectRepository,
    private val goalMapRepository: GoalMapRepository,
    private val statusRepository: StatusRepository
) : ViewModel() {

    var title: String? = null
    var topicId: Int? = null
    var planId: Int? = null
    var description: String? = null
    var performer: Int? = null
    var leader: Int? = null
    var startDate: String? = null
    var startTime: String? = null
    var endDate: String? = null
    var endTime: String? = null
    var files: ArrayList<File> = ArrayList()
    var goal: Int? = null

    var status: StatusData? = null
    var importance: Pair<String, String>? = null
    var yaqm: String? = null
    var participants: List<Int>? = null
    var spectators: List<Int>? = null
    var functionalGroup: List<Int>? = null
    var canEditTime: Boolean? = null
    var reminderDate: LocalDateTime? = null

    var repeatWeekRule = 0
    var repeatMonthRule = 0

    var goalList: ArrayList<GoalData>? = null
    var statusList: ArrayList<StatusData>? = null

    var repeat: String? = null
    var discussedTopics: MutableList<DiscussedTopic>? = null

    private val _createProjectLiveData = MutableLiveData<CreateProjectData>()
    val createProjectLiveData: LiveData<CreateProjectData> get() = _createProjectLiveData

    private val _allGoalLiveData = MutableLiveData<List<GoalData>>()
    val allGoalLiveData: LiveData<List<GoalData>> get() = _allGoalLiveData

//    private val _allStatusLiveData = MutableLiveData<List<StatusData>>()
//    val allStatusLiveData: LiveData<List<StatusData>> get() = _allStatusLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData


    init {
//        if (statusList == null) {
//            getProjectStatusList()
//        }
        if (goalList == null) {
            getGoals()
        }
    }


//    fun getProjectStatusList() {
//        _progressLiveData.value = true
//        viewModelScope.launch(Dispatchers.IO) {
//            statusRepository.getProjectStatusList().collect {
//                _progressLiveData.postValue(false)
//                it.onSuccess { statusData ->
//                    _allStatusLiveData.postValue(statusData)
//                    statusData.forEach {
//                        if (it.title == "Yangi") {
//                            status = it
//                        }
//                    }
//                    _progressLiveData.postValue(false)
//                }
//
//                it.onFailure { throwable ->
//                    _errorLiveData.postValue(throwable)
//                    _progressLiveData.postValue(false)
//                }
//            }
//        }
//    }

    private fun getGoals() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            goalMapRepository.getGoals().collect {
                _progressLiveData.postValue(false)
                it.onSuccess { goalMapData ->
                    _allGoalLiveData.postValue(goalMapData)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun createProject(
        title: String,
//        description: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        goal: Int,
        importance: String,
//        yaqm: String,
//        participants: List<Int>?,
        spectators: List<Int>?,
//        functional_group: List<Int>?,
        canEditTime: Boolean,
        repeat: String,
        plans: List<String>,
        reminderDate: String?,
        repeatRule: Int?,
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            createProjectRepository.createProject(
                title,
//                description,
                performer,
                leader,
                start_time,
                end_time,
                files,
                goal,
                importance,
//                yaqm,
//                participants,
                spectators,
//                functional_group,
                canEditTime,
                repeat,
                plans,
                reminderDate,
                repeatRule,
                topicId,
                planId
            ).collect {
                it.onSuccess { goalData ->
                    _createProjectLiveData.postValue(goalData)
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


