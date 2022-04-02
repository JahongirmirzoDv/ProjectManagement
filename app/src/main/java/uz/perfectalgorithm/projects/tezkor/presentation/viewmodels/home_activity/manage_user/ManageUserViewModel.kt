package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.manage_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.manage_user.ManageUsers
import uz.perfectalgorithm.projects.tezkor.domain.home.manage_users.ManageUsersRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject

@HiltViewModel
class ManageUserViewModel @Inject constructor(private val repository: ManageUsersRepository) :
    ViewModel() {

    private val _manageUsers = MutableLiveData<Event<List<ManageUsers>>>()
    val manageUsers: LiveData<Event<List<ManageUsers>>> get() = _manageUsers

    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    fun getManageUsers() {
        _progressLiveData.postValue(Event(true))
        viewModelScope.launch {
            repository.getManageUsers().collect {
                _progressLiveData.postValue(Event(false))
                it.onSuccess {
                    _manageUsers.postValue(Event(it))
                }
                it.onFailure {
                    _errorLiveData.postValue(Event(it))
                }
            }
        }
    }
}