package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.OthersRepository
import javax.inject.Inject



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: OthersRepository
) : ViewModel() {

    private var user: UserDataResponse.Data? = null

    fun setUserDataToVm(userDataResponse: UserDataResponse.Data) {
        user = userDataResponse
    }

    fun getUserDataFromVm() = user

    private val _userResponseLiveData = MutableLiveData<UserDataResponse.Data>()
    val userResponseLiveData: LiveData<UserDataResponse.Data> get() = _userResponseLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun getUserData() {

        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserData().collect {
                it.onSuccess { userData ->
                    _userResponseLiveData.postValue(userData)
                    _progressLiveData.postValue(false)
                }

                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }
            }
        }
    }
}