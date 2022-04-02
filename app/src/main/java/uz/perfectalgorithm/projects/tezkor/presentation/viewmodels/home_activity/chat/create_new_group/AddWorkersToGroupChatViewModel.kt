package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.create_new_group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.chat.ChatRepository
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject


/**
 * Craeted by Davronbek Raximjanov on 07.10.2021 18:18
 * **/

@HiltViewModel
class AddWorkersToGroupChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _allWorkersLiveData =
        MutableLiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>>()
    val allWorkersLiveData: LiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>> get() = _allWorkersLiveData

    fun getAllWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllWorkers().collect {
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

    private val _teamWorkersLiveData =
        MutableLiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>>()
    val teamWorkersLiveData: LiveData<List<AllWorkersShortDataResponse.WorkerShortDataItem>> get() = _teamWorkersLiveData

    fun getTeamWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTeamWorkers().collect {
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