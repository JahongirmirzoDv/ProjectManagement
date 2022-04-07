package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.week

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Range
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime
import org.joda.time.Days
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.EventWeeklyView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWeekBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.calendar.CalendarSectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.week.WeekViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.hexColor
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.toInt
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.touch
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.views.onGlobalLayoutHeight
import uz.perfectalgorithm.projects.tezkor.utils.views.usableScreenSize
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/***
 * bu Kalendar qismida hafta uchun ui qismi asosiy qism WeekView custom qilingan
 */

@AndroidEntryPoint
class WeekFragment : Fragment() {

    private var _binding: FragmentWeekBinding? = null
    private val binding: FragmentWeekBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(R.string.null_binding)
        )
    private val viewModel: WeekViewModel by viewModels()
    private lateinit var inflater: LayoutInflater
    private var weekTimeTS = 0L
    private var staffId = -1

    private var rowHeight = 0f
    private var weekContainerHeight = 1
    private var weekContainerWidth = 1f
    private var selectedGrid: View? = null
    private val events = ArrayList<CalendarResponse.Event>()
    private var dayColumns = ArrayList<RelativeLayout>()
    private val count = hashMapOf<Int, Int>()

    private val sharedVM: CalendarSharedVM by activityViewModels()

    @Inject
    lateinit var sectionsDialog: CalendarSectionsDialog

    private val getProgressData = EventObserver<Boolean> { it ->
        if (it) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }
    private var currentTimeView: View? = null


    private val getError = EventObserver<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private var allDayHolders = ArrayList<RelativeLayout>()
    private var allDayRows = ArrayList<HashSet<Int>>()

    private var todayColumnIndex = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weekTimeTS = requireArguments().getLong(WEEK_START_DATE_TIME)
        staffId = arguments?.getInt(STAFF_ID) ?: -1
    }

    private var eventTimeRanges = LinkedHashMap<String, ArrayList<EventWeeklyView>>()


    private val getWeekEvents = EventObserver<List<CalendarResponse.Event>> {
        events.clear()
        events.addAll(it)
        addEvents(events)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.inflater = inflater
        val height = resources.getDimension(R.dimen.week_view_height) * 24
        _binding = FragmentWeekBinding.inflate(layoutInflater, container, false).apply {
            weekView.layoutParams.height = height.toInt()
        }
        addDayColumns()

        return binding.root
    }

    private fun loadObservers() {
        viewModel.dayEventLiveData.observe(viewLifecycleOwner, getWeekEvents)
        viewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        viewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()
        addHours()
        setupDayLabels()
        addCurrentTimeIndicator()
        if (staffId > 0) {
            viewModel.getWeekEventsStaff(
                Formatter.getUTCDateTimeFromTS(weekTimeTS).withHourOfDay(0).withMinuteOfHour(0)
                    .withSecondOfMinute(0).withMillisOfSecond(0),
                staffId
            )
        } else {
            viewModel.getWeekEvents(
                Formatter.getUTCDateTimeFromTS(weekTimeTS).withHourOfDay(0).withMinuteOfHour(0)
                    .withSecondOfMinute(0).withMillisOfSecond(0),
                requireContext()
            )
        }
        loadObservers()
    }

    private fun loadView() {
        weekContainerWidth =
            (requireContext().usableScreenSize.x - resources.getDimension(R.dimen._40sdp)) / 7

        rowHeight = resources.getDimension(R.dimen.week_view_height)
    }

    override fun onResume() {
        super.onResume()
        initGrid()
    }

    private fun createEvent(startTime: String) {
        sectionsDialog.showSectionsBottomSheetDialog(requireContext(), {
            sharedVM.clickedCreateEvent(
                R.id.action_navigation_calendar_to_navigation_create_note,
                startTime
            )
        }, {
            sharedVM.clickedCreateEvent(
                R.id.action_navigation_calendar_to_navigation_create_task,
                startTime
            )
        },
            {
                sharedVM.clickedCreateEvent(
                    R.id.action_navigation_calendar_to_navigation_create_meeting,
                    startTime
                )
            },
            {
                sharedVM.clickedCreateEvent(
                    R.id.action_navigation_calendar_to_navigation_create_dating,
                    startTime
                )
            })
    }

    @SuppressLint("InflateParams")
    private fun addHours() {
        val heightHours = resources.getDimension(R.dimen.week_view_height)
        binding.weekViewHourContainer.removeAllViews()
        for (i in 1..24) {
            var hours = "$i:00"
            if (i == 24) {
                hours = ""
            }
            (layoutInflater.inflate(
                R.layout.weekly_view_hour_textview,
                null,
                false
            ) as AppCompatTextView).apply {
                text = hours
                height = heightHours.toInt()
                binding.weekViewHourContainer.addView(this)
            }
        }
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setupDayLabels() {
        var curDay = Formatter.getUTCDateTimeFromTS(weekTimeTS)
        val todayCode = Formatter.getDayCodeFromDateTime(DateTime())

        binding.weekLettersHolder.removeAllViews()
        val dayLetters = resources.getStringArray(R.array.week_days_short)
            .toMutableList() as ArrayList<String>

        for (i in 0 until 7) {
            val dayCode = Formatter.getDayCodeFromDateTime(curDay)
            val dayLetter = dayLetters[i]

            val label = inflater.inflate(
                R.layout.weekly_view_hour_textview,
                binding.weekLettersHolder, false
            ) as AppCompatTextView
            label.text = "$dayLetter\n${curDay.dayOfMonth}"
            if (dayCode == todayCode) {
                label.setTextColor(Color.WHITE)
                label.setBackgroundResource(R.drawable.back_button_blue)
                todayColumnIndex = i
            }
            binding.weekLettersHolder.addView(label)
            curDay = curDay.plusDays(1)
        }
        binding.weekLettersHolder.setOnTouchListener { view, event ->
            val index = (event.x * 7 / view.width).toInt()
            val date = Formatter.getDateTimeFromTS(weekTimeTS + index * DAY_SECONDS)
            findNavController().navigate(
                R.id.action_week_navigation_to_day_navigation,
                bundleOf(DAY_CODE to Formatter.getDayCodeFromDateTime(date), STAFF_ID to staffId)
            )
            return@setOnTouchListener false
        }
    }

    private fun addEvents(events: List<CalendarResponse.Event>) {
        initGrid()
        allDayHolders.clear()
        this.allDayRows.clear()
        eventTimeRanges.clear()
        this.allDayRows.add(HashSet())
        binding.eventContainer.removeAllViews()
        addNewLine()

        val minuteHeight = rowHeight / 60
        val minimalHeight = resources.getDimension(R.dimen.weekly_view_minimal_event_height).toInt()

        for (event in events) {
            val startDateTime = DateTime(event.startTime)
            val startDayCode = Formatter.getDayCodeFromDateTime(startDateTime)
            val endDateTime = DateTime(event.endTime)
            val endDayCode = Formatter.getDayCodeFromDateTime(endDateTime)

            if (event.getIsAllDay() || (startDayCode != endDayCode)) {
                continue
            }

            var currentDateTime = startDateTime
            var currentDayCode = Formatter.getDayCodeFromDateTime(currentDateTime)
            do {
                val startMinutes = when (currentDayCode == startDayCode) {
                    true -> (startDateTime.minuteOfDay)
                    else -> 0
                }
                var duration = when (currentDayCode == endDayCode) {
                    true -> (endDateTime.minuteOfDay - startMinutes)
                    else -> 1440
                }
                if (duration == 0) {
                    duration = 60
                }

                val range = Range(startMinutes, startMinutes + duration)

                val eventWeekly = EventWeeklyView(event.idLocalBase, range)


                if (!eventTimeRanges.containsKey(currentDayCode)) {
                    eventTimeRanges[currentDayCode] = ArrayList()
                }
                eventTimeRanges[currentDayCode]?.add(eventWeekly)

                currentDateTime = currentDateTime.plusDays(1)
                currentDayCode = Formatter.getDayCodeFromDateTime(currentDateTime)
            } while (currentDayCode.toInt() <= endDayCode.toInt())
        }

        dayevents@ for (event in events) {
            val startDateTime = DateTime(event.startTime)
            val startDayCode = Formatter.getDayCodeFromDateTime(startDateTime)
            val endDateTime = DateTime(event.endTime)
            val endDayCode = Formatter.getDayCodeFromDateTime(endDateTime)
            if (event.getIsAllDay() || (startDayCode != endDayCode)) {
                addAllDayEvent(event)
            } else {
                var currentDateTime = startDateTime
                var currentDayCode = Formatter.getDayCodeFromDateTime(currentDateTime)
                do {
                    val startMinutes = when (currentDayCode == startDayCode) {
                        true -> (startDateTime.minuteOfDay)
                        else -> 0
                    }
                    var duration = when (currentDayCode == endDayCode) {
                        true -> (endDateTime.minuteOfDay - startMinutes)
                        else -> 1440
                    }
                    if (duration <= 0) {
                        duration = 50
                    }


                    val range = Range(startMinutes, startMinutes + duration)
                    var overlappingEvents = 0
                    var currentEventOverlapIndex = 0
                    var foundCurrentEvent = false

                    eventTimeRanges[currentDayCode]!!.forEachIndexed { index, eventWeeklyView ->
                        if (eventWeeklyView.range.touch(range)) {
                            overlappingEvents++

                            if (eventWeeklyView.id == event.idLocalBase) {
                                foundCurrentEvent = true
                            }

                            if (!foundCurrentEvent) {
                                currentEventOverlapIndex++
                            }
                        }
                    }
                    val startDayIndex = startDateTime.dayOfWeek - 1

                    (inflater.inflate(
                        R.layout.view_event_calendar,
                        null,
                        false
                    ) as AppCompatTextView).apply {
                        text = event.title
                        setTextColor(Color.WHITE)
                        contentDescription = text
                        binding.eventContainer.addView(this)
                        y = startMinutes * minuteHeight

                        val yx = duration * minuteHeight

                        (layoutParams as RelativeLayout.LayoutParams).apply {
                            width = binding.eventContainer.width / 7
                            x = (binding.allEventContainer.width * startDayIndex / 7).toFloat()
                            minHeight = minimalHeight
                            height = yx.toInt()
                        }

                        setOnClickListener {
                            detailClickable(event)
                        }
                        setOnLongClickListener {

                            true
                        }


                        val drawable = this.background as GradientDrawable
                        drawable.setColor(Color.parseColor(requireContext().hexColor(event.type)))
                    }

                    currentDateTime = currentDateTime.plusDays(1)
                    currentDayCode = Formatter.getDayCodeFromDateTime(currentDateTime)
                } while (currentDayCode.toInt() <= endDayCode.toInt())
            }

        }
        addCurrentTimeIndicator()
    }


    private fun detailClickable(event: CalendarResponse.Event) {
        when (event.type) {
            "note" -> {
                sharedVM.clickedDetailEvent(
                    R.id.action_navigation_calendar_to_navigation_detail_note,
                    bundleOf(EVENT_ID to event.idType, CURRENT_DATE to event.startTime)
                )
            }
            "task" -> {
                sharedVM.clickedDetailEvent(
                    R.id.to_taskDetailsFragment,
                    bundleOf("taskId" to event.idType)
                )
            }
            "dating" -> {
                sharedVM.clickedDetailEvent(
                    R.id.to_datingDetailsFragment,
                    bundleOf("datingId" to event.idType)
                )
            }
            "meeting" -> {
                sharedVM.clickedDetailEvent(
                    R.id.to_meetingDetailsFragment,
                    bundleOf("meetingId" to event.idType)
                )
            }
        }
    }


//    private fun addAllDayEvent(event: CalendarResponse.Event) {
//        (inflater.inflate(
//            R.layout.view_event_calendar,
//            null,
//            false
//        ) as AppCompatTextView).apply {
//            text = event.title
//            setTextColor(Color.WHITE)
//            val startTime = DateTime(event.startTime)
//            val endTime = DateTime(event.endTime)
//            val startWeek = Formatter.getUTCDateTimeFromTS(weekTimeTS)
//            val endWeek = startWeek.plusDays(7)
//
//            var startIndex = 0
//            var endIndex = 7
//
//            if (startTime > startWeek) {
//                startIndex = startTime.dayOfWeek - 1
//            }
//
//            if (endTime < endWeek) {
//                endIndex = endTime.dayOfWeek
//            }
//            var doesEventFit: Boolean
//            val cnt = this@WeekFragment.allDayRows.size - 1
//            var wasEventHandled = false
//            var drawAtLine = 0
//
//            allDayHolders[drawAtLine].addView(this)
//
//            (layoutParams as RelativeLayout.LayoutParams).apply {
//                this.setMargins(4)
//                width = (endIndex - startIndex) * binding.allEventContainer.width / 7
//            }
//
//            binding.weekViewContainer.onGlobalLayoutWidth { width ->
//                x = (width / 7) * startIndex.toFloat()
//            }
//
//            setOnClickListener {
//                when (event.type) {
//                    "note" -> {
//                        findNavController().navigate(
//                            R.id.action_navigation_calendar_to_navigation_detail_note,
//                            bundleOf(EVENT_ID to event.idType)
//                        )
//                    }
//                    "task" -> {
//                        findNavController().navigate(
//                            CalendarFragmentDirections.toTaskDetailsFragment(
//                                event.idType
//                            )
//                        )
//                    }
//                    "dating" -> {
//                        findNavController().navigate(
//                            CalendarFragmentDirections.toDatingDetailsFragment(
//                                event.idType
//                            )
//                        )
//                    }
//                    "meeting" -> {
//                        findNavController().navigate(
//                            CalendarFragmentDirections.toMeetingDetailsFragment(
//                                event.idType
//                            )
//                        )
//                    }
//                }
//            }
//            val drawable = this.background as GradientDrawable
//            drawable.setColor(Color.parseColor(requireContext().hexColor(event.type)))
//        }
//    }

    private fun addAllDayEvent(event: CalendarResponse.Event) {
        (inflater.inflate(R.layout.view_event_calendar, null, false) as TextView).apply {
            text = event.title
            setTextColor(Color.WHITE)
            val startDateTime = DateTime(event.startTime)
            val endDateTime = DateTime(event.endTime)

            val minTS = Math.max(startDateTime.seconds(), weekTimeTS)
            val maxTS = Math.min(endDateTime.seconds(), weekTimeTS + 2 * WEEK_SECONDS)

            if (minTS == maxTS && (minTS - weekTimeTS == WEEK_SECONDS.toLong())) {
                return
            }

            val isStartTimeDay =
                Formatter.getDateTimeFromTS(maxTS) == Formatter.getDateTimeFromTS(maxTS)
                    .withTimeAtStartOfDay()
            val numDays = Days.daysBetween(
                Formatter.getDateTimeFromTS(minTS).toLocalDate(),
                Formatter.getDateTimeFromTS(maxTS).toLocalDate()
            ).days
            val daysCnt = if (numDays == 1 && isStartTimeDay) 0 else numDays
            val startDateTimeInWeek = Formatter.getDateTimeFromTS(minTS)
            val firstDayIndex =
                (startDateTimeInWeek.dayOfWeek - 1) % 7

            var doesEventFit: Boolean
            val cnt = allDayRows.size - 1
            var wasEventHandled = false
            var drawAtLine = 0
            for (index in 0..cnt) {
                doesEventFit = true
                drawAtLine = index
                val row = allDayRows[index]
                for (i in firstDayIndex..firstDayIndex + daysCnt) {
                    if (row.contains(i)) {
                        doesEventFit = false
                    }
                }

                for (dayIndex in firstDayIndex..firstDayIndex + daysCnt) {
                    if (doesEventFit) {
                        row.add(dayIndex)
                        wasEventHandled = true
                    } else if (index == cnt) {
                        if (allDayRows.size == index + 1) {
                            allDayRows.add(HashSet())
                            addNewLine()
                            drawAtLine++
                            wasEventHandled = true
                        }
                        allDayRows.last().add(dayIndex)
                    }
                }

                if (wasEventHandled) {
                    break
                }
            }

            val dayCodeStart = Formatter.getDayCodeFromDateTime(startDateTime).toInt()
            val dayCodeEnd = Formatter.getDayCodeFromDateTime(endDateTime).toInt()
            val dayOfWeek =
                dayColumns.indexOfFirst {
                    it.tag.toInt() == dayCodeStart || (it.tag.toInt() in (dayCodeStart + 1)..dayCodeEnd)
                }
            if (dayOfWeek == -1) {
                return
            }

            if (allDayHolders[drawAtLine].childCount <= 3) {
                allDayHolders[drawAtLine].addView(this)
            } else if (allDayHolders[drawAtLine].childCount == 4) {
                addAllDayEvent(event.copy(title = "+3", type = "none"))
            }

            val dayWidth = binding.eventContainer.width / 7
            (layoutParams as RelativeLayout.LayoutParams).apply {
                leftMargin = dayOfWeek * dayWidth
                bottomMargin = 1
                width = (dayWidth) * (daysCnt + 1)
            }

            setOnClickListener {
                detailClickable(event)
            }
            val drawable = this.background as GradientDrawable
            drawable.setColor(Color.parseColor(requireContext().hexColor(event.type)))
        }
    }

    private fun addNewLine() {
        val allDaysLine =
            inflater.inflate(R.layout.all_day_events_holder_line, null, false) as RelativeLayout
        binding.allEventContainer.addView(allDaysLine)
        allDayHolders.add(allDaysLine)
    }

    private fun addDayColumns() {
        binding.allEventContainer.removeAllViews()
        (0 until 7).forEach {
            val column = inflater.inflate(
                R.layout.weekly_view_day_column,
                binding.allEventContainer,
                false
            ) as RelativeLayout
            column.tag = Formatter.getUTCDayCodeFromTS(weekTimeTS + it * DAY_SECONDS)
            binding.allEventContainer.addView(column)
            dayColumns.add(column)
        }
    }

    private fun addCurrentTimeIndicator() {
        if (todayColumnIndex != -1) {
            val calendar = Calendar.getInstance()
            val minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

            var minuteHeight = 1f

            if (currentTimeView != null) {
                binding.eventContainer.removeView(currentTimeView)
            }


            currentTimeView = (layoutInflater.inflate(
                R.layout.calendar_now_marker,
                null,
                false
            ) as View).apply {
                binding.eventContainer.addView(this, 0)

                binding.weekViewContainer.onGlobalLayoutHeight { height, width ->
                    weekContainerHeight = height
                    minuteHeight = weekContainerHeight / 60f / 24f
                    y = minutes * minuteHeight
                    x = (width * todayColumnIndex / 7).toFloat()
                }

                (layoutParams as RelativeLayout.LayoutParams).apply {
                    height = 2
                    width = weekContainerWidth.toInt()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGrid() {
        val gestureDetector = getViewGestureDetector(binding.weekViewContainer)

        binding.weekViewContainer.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            true
        }
    }

    private fun getViewGestureDetector(view: ViewGroup): GestureDetector {
        return GestureDetector(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    selectedGrid?.animation?.cancel()
                    selectedGrid?.hide()

                    val hour = (e.y / rowHeight).toInt()
                    selectedGrid = (inflater.inflate(R.layout.week_grid_item, null, false)).apply {
                        view.addView(this)

                        layoutParams.height = rowHeight.toInt()
                        layoutParams.width = view.width / 7

                        y = hour * rowHeight
                        val index = (e.x / (view.width / 7)).toInt()

                        x = index * view.width / 7f


                        setOnClickListener {
                            val timeStamp =
                                Formatter.getDateTimeFromTS(weekTimeTS + index * DAY_SECONDS)
                                    .withTime(
                                        hour, 0, 0, 0
                                    )
                            createEvent(timeStamp.toString())
                        }
                    }
                    return super.onSingleTapUp(e)
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

