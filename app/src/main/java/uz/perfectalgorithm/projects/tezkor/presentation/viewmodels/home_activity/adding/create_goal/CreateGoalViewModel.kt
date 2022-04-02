package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_goal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.CreateGoalData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 28.06.2021 14:10
 **/

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val repository: GoalMapRepository
) : ViewModel() {
    var id: Int? = null
    var title: String? = null
    var description: String? = ""
    var performer: Int? = null
    var leader: Int? = null
    var startDate: String? = null
    var startTime: String? = null
    var endDate: String? = null
    var endTime: String? = null
    var files = ArrayList<File>()
    var folder: Int? = null
    var status: String? = "new"
    var participants: List<Int>? = null
    var spectators: List<Int>? = null
    var functionalGroup: List<Int>? = null
    var canEditTime: Boolean? = null

    var goalFolderList: ArrayList<ItemGoalMapData>? = null

    private val _createGoalLiveData = MutableLiveData<CreateGoalData>()
    val createGoalLiveData: LiveData<CreateGoalData> get() = _createGoalLiveData

    private val _allGoalMapLiveData = MutableLiveData<List<ItemGoalMapData>>()
    val allGoalMapLiveData: LiveData<List<ItemGoalMapData>> get() = _allGoalMapLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    init {
        Log.d("ASDF", "Before init")
        if (goalFolderList == null) {
            Log.d("ASDF", "After init")
            getGoalMaps()
        }
    }

    fun createGoal(
        title: String,
        description: String?,
        performer: Int,
        leader: Int,
        start_time: String,
        end_time: String,
        files: List<File>?,
        folder: Int,
        status: String,
        participants: List<Int>?,
        spectators: List<Int>?,
        functional_group: List<Int>?,
        canEditTime: Boolean
    ) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.createGoal(
                title,
                description,
                performer,
                leader,
                start_time,
                end_time,
                files,
                folder,
                status,
                participants,
                spectators,
                functional_group,
                canEditTime
            ).collect {

                Log.d("ZZZZ", "$it")

                it.onSuccess { goalData ->
                    Log.d("ZZZZ", "OnSuccess: $goalData")
                    _createGoalLiveData.postValue(goalData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    Log.d("ZZZZ", "onFailure: $throwable")
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)

                }

            }
        }
    }

    fun getGoalMaps() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGoalMaps().collect {
                _progressLiveData.postValue(false)
//                Log.d("TTTT", "$it")
                it.onSuccess { goalMapData ->
                    _allGoalMapLiveData.postValue(goalMapData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}