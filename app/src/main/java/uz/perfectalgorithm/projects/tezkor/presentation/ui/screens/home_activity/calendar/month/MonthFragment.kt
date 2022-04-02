package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.month

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.model.Events
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalDatabase
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.DayMonthly
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentMonthBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.month.MonthViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/***
 * Abduraxmonov Abdulla 11/09/2021
 * bu Kalendar qismida oy uchun ui qismi asosiy qism MonthView va MonthViewWrapper custom qilingan
 */

@AndroidEntryPoint
class MonthFragment : Fragment(), CoroutineScope {
    private var _binding: FragmentMonthBinding? = null
    private val binding: FragmentMonthBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(R.string.null_binding)
        )
    @Inject
    lateinit var storage: LocalStorage

    @Inject
    lateinit var credential: GoogleAccountCredential

    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar
    private val job = Job()
    private val viewModel: MonthViewModel by viewModels()
    private var startDateCode = ""
    private var staffId = -1
    private val days = ArrayList<DayMonthly>()

    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    private val getError = EventObserver<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }
    private val getDayEvents = Observer<List<CalendarResponse.Event>> { it ->

        addEvents(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(layoutInflater, container, false)
        startDateCode = requireArguments().getString(DAY_CODE, "")
        staffId = requireArguments().getInt(STAFF_ID)
        viewModel.getMonthDataWithoutEvents(startDateCode)
        viewModel.getMonthWithoutEvents.observe(viewLifecycleOwner, getDataObserver)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadObservers()
        if (staffId > 0) {
//            viewModel.getMonthEventsStaff(
//                Formatter.getDateTimeFromCode(startDateCode),
//                staffId
//            )
        } else {
            viewModel.getMonthEvents(Formatter.getDateTimeFromCode(startDateCode), requireContext())
        }
    }

    private fun loadObservers() {
        viewModel.monthEventLiveData.observe(viewLifecycleOwner, getDayEvents)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    fun getCalendar(): List<CalendarResponse.Event> {
        val list = ArrayList<CalendarResponse.Event>()
        launch {
            var pageToken: String? = null
            do {
                val events: Events =
                    client.events().list(storage.calendarID).setPageToken(pageToken).execute()
                val items = events.items
                var i = 0
                for (event in items) {
                    i++
//                    list.add(CalendarResponse.Event(event.end.))
                    Log.d("calendar page $i", "getCalendar: ${event.summary}")
                }
                pageToken = events.nextPageToken
            } while (pageToken != null)
        }
        return list
    }

    private fun addEvents(monthEvents: List<CalendarResponse.Event>) {

        val dayEvents = HashMap<String, ArrayList<CalendarResponse.Event>>()
        monthEvents.forEach {
            val startDate =
                if (DateTime(it.startTime) > Formatter.getDateTimeFromCode(days[0].code)) {
                    DateTime(it.startTime)
                } else {
                    Formatter.getDateTimeFromCode(days[0].code)
                }

            val endDate =
                if (DateTime(it.endTime) < Formatter.getDateTimeFromCode(days[days.size - 1].code)) {
                    DateTime(it.endTime)
                } else {
                    Formatter.getDateTimeFromCode(days[days.size - 1].code)
                }
            var currentDay = startDate
            while (currentDay.withTimeAtStartOfDay() <= endDate) {
                val dayCode = Formatter.getDayCodeFromDateTime(currentDay)
                val currentDayEvents = dayEvents[dayCode] ?: ArrayList()
                currentDayEvents.add(it)
                dayEvents[dayCode] = currentDayEvents
                currentDay = currentDay.plusDays(1)
            }
        }


        days.filter { dayEvents.keys.contains(it.code) }.forEach {
            it.dayEvents = dayEvents[it.code]!!
        }
        updateDays(days)
    }


    private val getDataObserver = EventObserver<List<DayMonthly>> {
        days.addAll(it)
        updateDays(days)
    }

    private fun updateDays(days: ArrayList<DayMonthly>) {
        binding.monthViewWrapper.updateDays(days, true) {
            (parentFragment as MonthHolderFragment).itemClick(it.code)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = job

}