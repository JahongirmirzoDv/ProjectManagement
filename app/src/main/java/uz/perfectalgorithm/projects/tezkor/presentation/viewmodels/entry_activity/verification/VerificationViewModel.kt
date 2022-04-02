package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.verification

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.VerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.reset_password.ResetPasswordVerificationRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.VerificationResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.reset_password.VerificationNewPasswordResponse
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> = _timer
    lateinit var t: CountDownTimer

    private var _verificationResponse =
        MutableStateFlow<DataWrapper<VerificationResponse.Result>>(DataWrapper.Empty())
    val verificationResponse: StateFlow<DataWrapper<VerificationResponse.Result>> =
        _verificationResponse.asStateFlow()

    private var _verificationNewPasswordResponse =
        MutableStateFlow<DataWrapper<VerificationNewPasswordResponse>>(DataWrapper.Empty())
    val verificationNewPasswordResponse: StateFlow<DataWrapper<VerificationNewPasswordResponse>> =
        _verificationNewPasswordResponse.asStateFlow()

    fun verification(verificationRequest: VerificationRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _verificationResponse.value = DataWrapper.Loading()
            val tokenRequest =
                TokenRequest(verificationRequest.password, verificationRequest.phoneNumber)

            repository.verificationUserNew(verificationRequest).let { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> _verificationResponse.value = dataWrapper
                    is DataWrapper.Loading -> Unit
                    is DataWrapper.Success -> {
                        repository.signInNew(tokenRequest).let { wrapper ->
                            when (wrapper) {
                                is DataWrapper.Empty -> Unit
                                is DataWrapper.Error -> _verificationResponse.value =
                                    DataWrapper.Error(wrapper.error)
                                is DataWrapper.Loading -> Unit
                                is DataWrapper.Success -> _verificationResponse.value = dataWrapper
                            }
                        }
                    }
                }
            }
        }

    fun verificationNewPassword(verificationRequest: ResetPasswordVerificationRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _verificationResponse.value = DataWrapper.Loading()
            val tokenRequest =
                TokenRequest(verificationRequest.password, verificationRequest.phoneNumber)

            repository.resetPassword(verificationRequest).let { dataWrapper ->
                when (dataWrapper) {
                    is DataWrapper.Empty -> Unit
                    is DataWrapper.Error -> _verificationNewPasswordResponse.value = dataWrapper
                    is DataWrapper.Loading -> Unit
                    is DataWrapper.Success -> {
                        repository.signInNew(tokenRequest).let { wrapper ->
                            when (wrapper) {
                                is DataWrapper.Empty -> Unit
                                is DataWrapper.Error -> _verificationNewPasswordResponse.value =
                                    DataWrapper.Error(wrapper.error)
                                is DataWrapper.Loading -> Unit
                                is DataWrapper.Success -> _verificationNewPasswordResponse.value =
                                    dataWrapper
                            }
                        }
                    }
                }
            }
        }

//    private val _verificationLiveData = MutableLiveData<Event<VerificationResponse.Result>>()
//    val verificationLiveData: LiveData<Event<VerificationResponse.Result>> get() = _verificationLiveData
//
//    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
//    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<Event<String>>()
//    val errorLiveData: LiveData<Event<String>> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Event<Unit>>()
//    val notConnectionLiveData: LiveData<Event<Unit>> get() = _notConnectionLiveData


    fun startTimer(finish: Long, tick: Long) {
        t = object : CountDownTimer(finish, tick) {
            override fun onTick(millisUntilFinished: Long) {
                val remainedSecs = millisUntilFinished / 1000
                if (remainedSecs % 60 >= 10) {
                    _timer.postValue(
                        "" + remainedSecs / 60 + ":" + remainedSecs % 60
                    )
                } else {
                    _timer.postValue(
                        "" + remainedSecs / 60 + ":0" + remainedSecs % 60
                    )
                }
            }

            override fun onFinish() {
                _timer.postValue("00:00")
                cancel()
            }
        }.start()
    }

//    fun verification(verificationRequest: VerificationRequest) {
//        if (isConnected()) {
//            val tokenRequest =
//                TokenRequest(verificationRequest.password, verificationRequest.phoneNumber)
//            _progressLiveData.value = Event(true)
//
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.verificationUser(verificationRequest).collect {
//                    it.onSuccess { verificationResponse ->
//                        repository.signIn(tokenRequest).collect { it ->
//                            _progressLiveData.postValue(Event(false))
//                            it.onSuccess {
//                                _verificationLiveData.postValue(Event(verificationResponse))
//                            }
//                            it.onFailure { throwable ->
//                                _errorLiveData.postValue(Event(throwable.message ?: "Error"))
//                            }
//                        }
//                    }
//                    it.onFailure { throwable ->
//                        _errorLiveData.postValue(Event(throwable.message ?: "Error"))
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Event(Unit)
//    }

    override fun onCleared() {
        super.onCleared()
        t.cancel()
    }

}
