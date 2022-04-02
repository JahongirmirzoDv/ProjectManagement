package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.notification.NotificationResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.notification.NotificationRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(private val notificationRepository: NotificationRepository) :
    ViewModel() {

    private val _notificationLiveData =
        MutableLiveData<Event<PagingData<NotificationResponse.NotificationData>>>()
    val notificationLiveData: LiveData<Event<PagingData<NotificationResponse.NotificationData>>> get() = _notificationLiveData

    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    fun getNotification() {
        _progressLiveData.postValue(Event(true))

        viewModelScope.launch {
            notificationRepository.getNotification().collect {
                _progressLiveData.postValue(Event(false))
                _notificationLiveData.postValue(Event(it))
            }
        }
    }


}