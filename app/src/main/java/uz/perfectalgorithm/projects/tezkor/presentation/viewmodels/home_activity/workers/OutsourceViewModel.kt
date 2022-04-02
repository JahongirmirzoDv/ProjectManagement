package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.AddUserToFavouritesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.RemoveUserFromFavouritesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import javax.inject.Inject


/**
 * Craeted by Davronbek Raximjanov on 22.06.2021
 * **/

@HiltViewModel
class OutsourceViewModel @Inject constructor(
    private val repository: WorkersRepository
) : ViewModel() {

    private val _outsourceWorkersLiveData = MutableLiveData<List<AllWorkersResponse.DataItem>>()
    val outsourceWorkersLiveData: LiveData<List<AllWorkersResponse.DataItem>> get() = _outsourceWorkersLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _addUserToFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val addUserToFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _addUserToFavouritesLiveData

    private val _removeFromFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val removeFromFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _removeFromFavouritesLiveData

    fun getOutsourceWorkers() {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOutsourceWorkers().collect {
                it.onSuccess { listProjects ->
                    _outsourceWorkersLiveData.postValue(listProjects)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }


    fun addToFavourite(userId: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUserToFavourites(AddUserToFavouritesRequest(userId)).collect {
                it.onSuccess { staffData ->
                    _addUserToFavouritesLiveData.postValue(staffData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                    _progressLiveData.postValue(false)
                }
            }
        }
    }

    fun removeFromFavourites(userId: Int) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeUserFromFavourites(RemoveUserFromFavouritesRequest(userId))
                .collect {
                    it.onSuccess { staffData ->
                        _removeFromFavouritesLiveData.postValue(staffData)
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