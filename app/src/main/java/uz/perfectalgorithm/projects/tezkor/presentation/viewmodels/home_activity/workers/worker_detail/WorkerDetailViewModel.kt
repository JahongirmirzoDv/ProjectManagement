package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.worker_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.AddUserToFavouritesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.RemoveUserFromFavouritesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.WorkerDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * Craeted by Davronbek Raximjanov on 03.07.2021
 **/

@HiltViewModel
class WorkerDetailViewModel @Inject constructor(
    private val repository: WorkersRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _addUserToFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val addUserToFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _addUserToFavouritesLiveData

    private val _removeFromFavouritesLiveData = MutableLiveData<WorkerDataResponse.WorkerData>()
    val removeFromFavouritesLiveData: LiveData<WorkerDataResponse.WorkerData> get() = _removeFromFavouritesLiveData

    private val _staff = MutableStateFlowWrapper<AllWorkersResponse.DataItem>()
    val staff: StateFlowWrapper<AllWorkersResponse.DataItem> get() = _staff.asStateFlow()

    fun loadStaff(staffId: Int) = launchCoroutine {
        _staff.value = DataWrapper.Loading()
        _staff.value = repository.getStaff(staffId)
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