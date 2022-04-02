package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.day

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.calendar.CalendarRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(private val repository: CalendarRepository) : ViewModel() {


    private val _dayEventLiveData = MutableLiveData<Event<List<CalendarResponse.Event>>>()
    val dayEventLiveData: LiveData<Event<List<CalendarResponse.Event>>> get() = _dayEventLiveData

    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    fun getDayEvents(day: DateTime, context: Context) {
        _progressLiveData.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getDayEvents(day, context).collect {
                it.onSuccess { data ->
                    _dayEventLiveData.postValue(Event(data))
                    _progressLiveData.postValue(Event(false))

                }
                it.onFailure {
                    _errorLiveData.postValue(Event(it))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }

    fun getDayEventsStaff(staffId: Int, day: DateTime) {
        _progressLiveData.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getDayEventsStaff(day, staffId).collect {
                it.onSuccess { data ->
                    _dayEventLiveData.postValue(Event(data))
                    _progressLiveData.postValue(Event(false))
                }
                it.onFailure {
                    _errorLiveData.postValue(Event(it))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }
}