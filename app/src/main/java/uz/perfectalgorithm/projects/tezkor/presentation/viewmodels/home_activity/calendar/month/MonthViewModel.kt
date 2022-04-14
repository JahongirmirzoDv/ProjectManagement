package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.month

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
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.domain.home.calendar.CalendarRepository
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    var client: com.google.api.services.calendar.Calendar,
    private val calendarRepository: CalendarRepository,
) :
    ViewModel() {

    private val _getMonthWithoutEvents =
        MutableLiveData<Event<List<DayMonthly>>>()
    val getMonthWithoutEvents: LiveData<Event<List<DayMonthly>>> get() = _getMonthWithoutEvents

    fun getMonthDataWithoutEvents(monthCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            calendarRepository.getMonthWithoutEvent(monthCode).collect {
                it.onSuccess {
                    _getMonthWithoutEvents.postValue(Event(it))
                }
            }
        }
    }


    private val _monthEventLiveData = MutableLiveData<List<CalendarResponse.Event>>()
    val monthEventLiveData: LiveData<List<CalendarResponse.Event>> get() = _monthEventLiveData

    private val _progressLiveData = MutableLiveData<Event<Boolean>>()
    val progressLiveData: LiveData<Event<Boolean>> get() = _progressLiveData

    private val _errorLiveData = MutableLiveData<Event<Throwable>>()
    val errorLiveData: LiveData<Event<Throwable>> get() = _errorLiveData

    fun getMonthEvents(month: DateTime, context: Context) {
        _progressLiveData.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            calendarRepository.getMonthEvents(month, context).collect {
                it.onSuccess { data ->
                    _monthEventLiveData.postValue(data)
                    _progressLiveData.postValue(Event(false))

                }
                it.onFailure {
                    _errorLiveData.postValue(Event(it))
                    _progressLiveData.postValue(Event(false))
                }
            }
        }
    }

//    fun getMonthEventsStaff(month: DateTime, staffId: Int) {
//        _progressLiveData.postValue(Event(true))
//
//        viewModelScope.launch(Dispatchers.IO) {
//            calendarRepository.getMonthEventsStaff(month, staffId).collect {
//                it.onSuccess { data ->
//                    _monthEventLiveData.postValue(Event(data))
//                    _progressLiveData.postValue(Event(false))
//                }
//                it.onFailure {
//                    _errorLiveData.postValue(Event(it))
//                    _progressLiveData.postValue(Event(false))
//                }
//            }
//        }
//    }
}