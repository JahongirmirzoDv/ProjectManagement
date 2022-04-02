package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.domain.home.notification.NotificationRepository
import javax.inject.Inject

/**
 * Created by Jasurbek Kurganbaev on 16.06.2021 17:28
 **/

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val notificationRepository: NotificationRepository) :
    ViewModel() {

    private val _openCreateNoteScreen = MutableLiveData<Unit>()
    val openCreateNoteScreen: LiveData<Unit> get() = _openCreateNoteScreen

    private val _openCreateTaskScreen = MutableLiveData<Unit>()
    val openCreateTaskScreen: LiveData<Unit> get() = _openCreateTaskScreen

    private val _openCreateProjectScreen = MutableLiveData<Unit>()
    val openCreateProjectScreen: LiveData<Unit> get() = _openCreateProjectScreen

    private val _openCreateMeeting = MutableLiveData<Unit>()
    val openCreateMeeting: LiveData<Unit> get() = _openCreateMeeting

    private val _openCreateEventScreen = MutableLiveData<Unit>()
    val openCreateEventScreen: LiveData<Unit> get() = _openCreateEventScreen

    private val _openCreateUchrashuvScreen = MutableLiveData<Unit>()
    val openCreateUchrashuvScreen: LiveData<Unit> get() = _openCreateUchrashuvScreen

    private val _notificationCount = MutableLiveData<Int>()
    val notificationCount: LiveData<Int> get() = _notificationCount


    fun openCreateNoteScreen() {
        _openCreateNoteScreen.value = Unit
    }


    fun openCreateTaskScreen() {
        _openCreateTaskScreen.value = Unit
    }

    fun openCreateProjectScreen() {
        _openCreateProjectScreen.value = Unit
    }

    fun openCreateMeeting() {
        _openCreateMeeting.value = Unit
    }

    fun openCreateEventScreen() {
        _openCreateEventScreen.value = Unit
    }

    fun openCreateUchrashuvScreen() {
        _openCreateUchrashuvScreen.value = Unit
    }

    fun getNotificationCount() {
        viewModelScope.launch {
            notificationRepository.getNotificationCount().collect {
                it.onSuccess { countResponse ->
                    _notificationCount.postValue(countResponse.count)
                }
                it.onFailure {
                    _notificationCount.postValue(0)
                }
            }
        }
    }
}
