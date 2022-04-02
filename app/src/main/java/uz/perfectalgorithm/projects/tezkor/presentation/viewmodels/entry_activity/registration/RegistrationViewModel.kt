package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.RegistrationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.RegistrationResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private var _registerResponse =
        MutableSharedFlow<DataWrapper<RegistrationResponse.Result>>(replay = 0)
    val registerResponse: SharedFlow<DataWrapper<RegistrationResponse.Result>> =
        _registerResponse.asSharedFlow()

    fun registration(phone: String) = viewModelScope.launch(Dispatchers.IO) {
        _registerResponse.emit(DataWrapper.Loading())
        val registrationRequest = RegistrationRequest(phoneNumber = phone)
        _registerResponse.emit(repository.registrationUserNew(registrationRequest))
    }

//    private val _registrationLiveData = MutableLiveData<Event<RegistrationResponse.Result>>()
//    val registrationLiveData: LiveData<Event<RegistrationResponse.Result>> get() = _registrationLiveData
//
//    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
//    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<Event<String>>()
//    val errorLiveData: LiveData<Event<String>> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Event<Unit>>()
//    val notConnectionLiveData: LiveData<Event<Unit>> get() = _notConnectionLiveData


//    fun registration(phone: String) {
//        if (isConnected()) {
//            val registrationRequest = RegistrationRequest(phoneNumber = phone)
//            _progressLiveData.value = Event(true)
//
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.registrationUser(registrationRequest).collect {
//                    _progressLiveData.postValue(Event(true))
//                    it.onSuccess { it ->
//                        _registrationLiveData.postValue(Event(it))
//                    }
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(Event(throwable.message ?: "Error"))
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Event(Unit)
//    }
}