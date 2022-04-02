package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.TokenRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.AuthResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject


/**
 * Craeted by Davronbek Raximjanov on 17.06.2021
 * **/

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private var _loginResponse =
        MutableStateFlow<DataWrapper<AuthResponse.UserData>>(DataWrapper.Empty())
    val loginResponse: StateFlow<DataWrapper<AuthResponse.UserData>> = _loginResponse.asStateFlow()

    fun login(phone: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _loginResponse.value = DataWrapper.Loading()
        val tokenRequest = TokenRequest(phoneNumber = phone, password = password)
        _loginResponse.value = repository.signInNew(tokenRequest)
    }

//    private val _loginLiveData = MutableLiveData<Event<AuthResponse.UserData>>()
//    val loginLiveData: LiveData<Event<AuthResponse.UserData>> get() = _loginLiveData
//
//    private val _loginOwnerLiveData = MutableLiveData<Event<AuthResponse.UserData>>()
//    val loginOwnerLiveData: LiveData<Event<AuthResponse.UserData>> get() = _loginOwnerLiveData
//
//    private val _progressLiveData = MutableLiveData<Boolean>()
//    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<String>()
//    val errorLiveData: LiveData<String> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Unit>()
//    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData

//    fun login(phone: String, password: String) {
//        if (isConnected()) {
//            val tokenRequest = TokenRequest(phoneNumber = phone, password = password)
//            _progressLiveData.value = true
//
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.signIn(tokenRequest).collect {
//                    _progressLiveData.postValue(false)
//                    it.onSuccess { userData ->
//                        if (userData.role == RoleEnum.OWNER.text) {
//                            _loginOwnerLiveData.postValue(Event(userData))
//                        } else {
//                            _loginLiveData.postValue(Event(userData))
//                        }
//                    }
//
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(throwable.message)
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Unit
//    }
}