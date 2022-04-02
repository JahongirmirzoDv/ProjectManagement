package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.workers.edit_contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.workers.UpdateContactDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.UpdateContactDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.workers.WorkersRepository
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.MutableStateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.StateFlowWrapper
import uz.perfectalgorithm.projects.tezkor.utils.coroutinescope.launchCoroutine
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 14.09.2021 15:06
 **/

@HiltViewModel
class EditContactViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val storage: LocalStorage
) : ViewModel() {

    var oldFirstName = ""
    var oldLastName = ""

    private val _updateContactLiveData = MutableLiveData<UpdateContactDataResponse.Data>()
    val updateContactLiveData: LiveData<UpdateContactDataResponse.Data> get() = _updateContactLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _notConnectionLiveData = MutableLiveData<Unit>()
    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

    private val _staff = MutableStateFlowWrapper<AllWorkersResponse.DataItem>()
    val staff: StateFlowWrapper<AllWorkersResponse.DataItem> get() = _staff.asStateFlow()

    fun loadStaff(staffId: Int) = launchCoroutine {
        _staff.value = DataWrapper.Loading()
        _staff.value = repository.getStaff(staffId)
    }

    fun updateContactData(updateContactDataRequest: UpdateContactDataRequest) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateContactData(updateContactDataRequest).collect {
                it.onSuccess { updated ->
                    _updateContactLiveData.postValue(updated)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }
}