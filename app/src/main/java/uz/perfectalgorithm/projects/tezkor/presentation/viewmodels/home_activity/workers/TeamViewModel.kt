package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject


/**
 * Craeted by Davronbek Raximjanov on 17.06.2021
 * **/

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: WorkersRepository
) : ViewModel() {

    private val _teamWorkersLiveData = MutableLiveData<List<AllWorkersShort>>()
    val teamWorkersLiveData: LiveData<List<AllWorkersShort>> get() = _teamWorkersLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getTeamWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTeamWorkersShortFlow().collect {
                it.onSuccess { listProjects ->
                    _teamWorkersLiveData.postValue(listProjects)
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