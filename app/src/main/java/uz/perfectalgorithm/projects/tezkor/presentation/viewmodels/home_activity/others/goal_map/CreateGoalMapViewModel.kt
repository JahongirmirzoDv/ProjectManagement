package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.goal_map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.goal.ItemGoalMapData
import uz.perfectalgorithm.projects.tezkor.domain.home.others.goal_map.GoalMapRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 28.06.2021 14:20
 **/

@HiltViewModel
class CreateGoalMapViewModel @Inject constructor(
    private val repository: GoalMapRepository
) : ViewModel() {

    private val _getAllGoalMapsLiveData = MutableLiveData<List<ItemGoalMapData>>()
    val getAllGoalMapsLiveData: LiveData<List<ItemGoalMapData>> get() = _getAllGoalMapsLiveData

    private val _goalMapResponseLiveData = MutableLiveData<ItemGoalMapData>()
    val goalMapResponseLiveData: LiveData<ItemGoalMapData> get() = _goalMapResponseLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun createGoalMap(title: String) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.createGoalMap(title).collect {
                Log.d("TTTT", "$it")
                it.onSuccess { userData ->
                    _goalMapResponseLiveData.postValue(userData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }

    fun getAllGoalMapData() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGoalMaps().collect {
                Log.d("TTTT", "$it")
                it.onSuccess { goalsMapData ->
                    _getAllGoalMapsLiveData.postValue(goalsMapData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}