package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map.detail_goal_map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.GoalsItem
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 16.07.2021 11:06
 **/
@HiltViewModel
class DetailGoalMapViewModel @Inject constructor(
    private val goalMapRepository: GoalMapRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _getAllGoalMapsProjectsLiveData = MutableLiveData<List<GoalsItem>>()
    val getAllGoalMapsProjectsLiveData: LiveData<List<GoalsItem>> get() = _getAllGoalMapsProjectsLiveData


    fun getGoalStructure(id: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            goalMapRepository.getGoalMap(id).collect {

                Log.d("TTTT", "$it")
                it.onSuccess { userData ->
                    _getAllGoalMapsProjectsLiveData.postValue(userData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }
}