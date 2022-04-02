package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.entry_activity.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.DataWrapper
import uz.perfectalgorithm.projects.tezkor.domain.entry.AuthRepository
import javax.inject.Inject

/**
 * Craeted by Davronbek Raximjanov on 18.06.2021
 * **/

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private var _tokenResponse =
        MutableStateFlow<DataWrapper<Unit>>(DataWrapper.Empty())
    val tokenResponse: StateFlow<DataWrapper<Unit>> = _tokenResponse.asStateFlow()

    fun refreshToken() = viewModelScope.launch(Dispatchers.IO) {
        _tokenResponse.value = DataWrapper.Loading()
        _tokenResponse.value = repository.refreshTokenNew()
    }

//    private val _refreshTokenLiveData = MutableLiveData<Unit>()
//    val refreshTokenLiveData: LiveData<Unit> get() = _refreshTokenLiveData
//
//    private val _progressLiveData = MutableLiveData<Boolean>()
//    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
//
//    private val _errorLiveData = MutableLiveData<String>()
//    val errorLiveData: LiveData<String> get() = _errorLiveData
//
//    private val _notConnectionLiveData = MutableLiveData<Unit>()
//    val notConnectionLiveData: LiveData<Unit> get() = _notConnectionLiveData
//
//    fun refreshToken() {
//        if (isConnected()) {
//            _progressLiveData.value = true
//
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.refreshToken().collect {
//                    _progressLiveData.postValue(false)
//                    if (it.isSuccess) {
//                        _refreshTokenLiveData.postValue(Unit)
//                    } else it.exceptionOrNull()?.let { throwable ->
//                        _errorLiveData.postValue(throwable.message)
//                    }
//                }
//            }
//        } else _notConnectionLiveData.value = Unit
//    }
}