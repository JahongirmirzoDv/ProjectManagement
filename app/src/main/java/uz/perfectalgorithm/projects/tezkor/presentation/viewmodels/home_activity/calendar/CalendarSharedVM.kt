package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.utils.livedata.Event
import javax.inject.Inject

@HiltViewModel
class CalendarSharedVM @Inject constructor(val googleCalendarClient: com.google.api.services.calendar.Calendar) : ViewModel() {


    val rowHeightLiveData = MutableLiveData<Float>()
    val changeStateLiveData = MutableLiveData<Int>()

    var setSelectionItem = MutableLiveData<Pair<DateTime, Boolean>?>()


    val createEventLiveData = MutableLiveData<Event<Pair<Int, String>>>()
    val clickedDetailLiveData = MutableLiveData<Event<Pair<Int, Bundle>>>()

    init {
        setSelectionItem.value = Pair(DateTime.now(), false)
    }

    fun selectionDate(date: DateTime, isYear: Boolean) {
        setSelectionItem.value = Pair(date, isYear)
    }

    fun clickedCreateEvent(action: Int, date: String) {
        createEventLiveData.value = Event(Pair(action, date))
    }

    fun clickedDetailEvent(action: Int, bundle: Bundle) {
        clickedDetailLiveData.value = Event(Pair(action, bundle))
    }

    fun changeHeight(rowHeight: Float) {
        rowHeightLiveData.value = rowHeight
    }

    fun changeState(state: Int) {
        changeStateLiveData.value = state
    }

    val clickMonthLiveData = MutableLiveData<Event<String>>()


    fun clickedMonthEvent(dayCode: String) {
        clickMonthLiveData.value = Event(dayCode)
    }


}