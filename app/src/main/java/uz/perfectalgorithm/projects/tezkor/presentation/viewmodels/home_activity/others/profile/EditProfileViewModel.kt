package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.others.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.auth.UpdateUserDataRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UpdateUserDataResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.others.profile.UserDataResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.others.OthersRepository
import java.io.File
import javax.inject.Inject

/**
 * Created by Davronbek Raximjanov on 28.08.2021 15:16
 **/

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: OthersRepository,
    private val storage: LocalStorage
) : ViewModel() {

    var oldFirstName = ""
    var oldLastName = ""
    var oldBirthDate = ""

    private val _userResponseLiveData = MutableLiveData<UserDataResponse.Data>()
    val userResponseLiveData: LiveData<UserDataResponse.Data> get() = _userResponseLiveData

    private val _updateUserLiveData = MutableLiveData<UpdateUserDataResponse.Data>()
    val updateUserLiveData: LiveData<UpdateUserDataResponse.Data> get() = _updateUserLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    fun setFullNameToLocal(firstName: String, lastName: String) {
        storage.userFirstName = firstName
        storage.userLastName = lastName
    }

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

    fun updateUserData(updateUserDataRequest: UpdateUserDataRequest, image: File?) {
        _progressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserData(updateUserDataRequest, image).collect {
                it.onSuccess { userData ->
                    _updateUserLiveData.postValue(userData)
                    _progressLiveData.postValue(false)
                }
                it.onFailure { throwable ->
                    _errorLiveData.postValue(throwable)
                }

            }
        }
    }
}