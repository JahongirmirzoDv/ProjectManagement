package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.day


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Range
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar.EventWeeklyView
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.calendar.CalendarResponse
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDayBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.calendar.EventItemClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.calendar.EventsAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.calendar.CalendarSectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.day.DayViewModel
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.converter.dpToPx
import uz.perfectalgorithm.projects.tezkor.utils.converter.getScreenHeight
import uz.perfectalgorithm.projects.tezkor.utils.converter.pxToDp
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.hexColor
import uz.perfectalgorithm.projects.tezkor.utils.extensions.calendar.touch
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import uz.perfectalgorithm.projects.tezkor.utils.views.onGlobalLayout
import uz.perfectalgorithm.projects.tezkor.utils.views.onGlobalLayoutHeight
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.max

/***
 * bu Kalendar qismida kun uchun ui qismi asosiy qism DayView custom qilingan
 */

@AndroidEntryPoint
class DayFragment : Fragment(), EventItemClickListener {

    private var _binding: FragmentDayBinding? = null
    private val binding: FragmentDayBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(
                R.string.null_binding
            )
        )

    private var selectedGrid: View? = null

    private var isToday = false
    private var dayCode = ""
    private var staffId = -1
    private var dayContainerHeight = 1
    private val dayViewModel: DayViewModel by viewModels()
    private lateinit var inflater: LayoutInflater
    private var rowHeight = 0f
    private var newRowHeight = 0f
    private var currentTimeView: View? = null
    private var allDayHolders = ArrayList<RelativeLayout>()
    private var allDayRows = ArrayList<HashSet<Int>>()
    private var eventTimeRanges = LinkedHashMap<String, ArrayList<EventWeeklyView>>()
    private val events = ArrayList<CalendarResponse.Event>()
    private var viewState = 0
    private var wasFragmentInit = false

    private var isZooming = true

    private val maxHeight = 200f.dpToPx()

    private lateinit var adapter: EventsAdapter

    @Inject
    lateinit var sectionsDialog: CalendarSectionsDialog

    @Inject
    lateinit var storage: LocalStorage

    private val sharedVM: CalendarSharedVM by activityViewModels()

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

    private val getDayEvents = EventObserver<List<CalendarResponse.Event>> { it ->
        events.clear()
        events.addAll(it)
        addEvents(events)
        if (it.isNotEmpty()) {
            binding.rvEvent.show()
            binding.tvNoEvent.hide()
            adapter.submitList(events)
        } else {
            binding.rvEvent.hide()
            binding.tvNoEvent.show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayCode =
            requireArguments().getString(DAY_CODE, Formatter.getDayCodeFromDateTime(DateTime.now()))
        staffId = requireArguments().getInt(STAFF_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.inflater = inflater
        _binding = FragmentDayBinding.inflate(inflater, container, false).apply {
            dayViewContainer.onGlobalLayout {
                dayContainerHeight = dayViewContainer.height
            }
        }
        wasFragmentInit = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = EventsAdapter(requireContext(), this)
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = adapter
        loadView()
        addHours()
        initGrid()
        loadObservers()
        loadActions()
        checkViewState()
        if (staffId > 0) {
            dayViewModel.getDayEventsStaff(
                staffId,
                Formatter.getDateTimeFromCode(dayCode)
            )
        } else {
            dayViewModel.getDayEvents(Formatter.getDateTimeFromCode(dayCode), requireContext())
        }
        getScrollManage()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getScrollManage() {

        val scaleDetector = getViewScaleDetector()

        binding.weekEventsScrollview.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            return@setOnTouchListener (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_DOWN) && isZooming

        }
    }

    private fun loadActions() {
        binding.lnToday.setOnClickListener {
            if (viewState == 0) {
                binding.allEventContainer.visibility = View.INVISIBLE
                viewState = 1
//                sharedVM.changeState(1)
            } else {
                binding.allEventContainer.visibility = View.VISIBLE
                viewState = 0
//                sharedVM.changeState(0)
            }
            binding.viewFlipper.showNext()
        }
    }

    private fun checkViewState() {
        if (viewState == 1) {
            binding.viewFlipper.showNext()
            binding.allEventContainer.visibility = View.INVISIBLE
        }
    }

    private fun loadObservers() {
        dayViewModel.dayEventLiveData.observe(viewLifecycleOwner, getDayEvents)
        dayViewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        dayViewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    private fun addEvents(events: List<CalendarResponse.Event>) {
        initGrid()
        allDayHolders.clear()
        allDayRows.clear()
        eventTimeRanges.clear()
        allDayRows.add(HashSet())
        binding.eventContainer.removeAllViews()

        val minuteHeight = rowHeight / 60
        val minimalHeight = resources.getDimension(R.dimen.weekly_view_minimal_event_height).toInt()
        val density = Math.round(resources.displayMetrics.density)

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
                    duration = 50
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

                    eventTimeRanges[currentDayCode]!!.forEachIndexed { _, eventWeeklyView ->
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
                        (layoutParams as RelativeLayout.LayoutParams).apply {
                            width = binding.eventContainer.width - 1
                            width /= overlappingEvents.coerceAtLeast(1)
                            if (overlappingEvents > 1) {
                                x = width * currentEventOverlapIndex.toFloat()
                                if (currentEventOverlapIndex != 0) {
                                    x += density
                                }

                                width -= density
                                if (currentEventOverlapIndex + 1 != overlappingEvents) {
                                    if (currentEventOverlapIndex != 0) {
                                        width -= density
                                    }
                                }
                            }
                            height = (duration * minuteHeight).toInt()

                        }

                        setOnClickListener {
                            detailClickable(event)
                        }

//                        setOnLongClickListener { view ->
//                            val shadowBuilder = View.DragShadowBuilder(view)
//                            val clipData = ClipData.newPlainText(
//                                "WEEKLY_EVENT_ID_LABEL",
//                                event.idType.toString()
//                            )
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                view.startDragAndDrop(clipData, shadowBuilder, null, 0)
//                            } else {
//                                @Suppress("DEPRECATION")
//                                view.startDrag(clipData, shadowBuilder, null, 0)
//                            }
////                            view.inVisible()
//                            return@setOnLongClickListener true
//                        }
//
//                        setOnDragListener { _, dragEvent ->
//                            when (dragEvent.action) {
//                                DragEvent.ACTION_DRAG_STARTED -> {
//                                    timberLog("started", "KKKK")
//                                    true
//                                }
//                                DragEvent.ACTION_DRAG_ENTERED -> {
//                                    timberLog("Entered", "KKKK")
//                                    true
//                                }
//                                DragEvent.ACTION_DRAG_LOCATION -> {
//
//                                    timberLog("Location", "KKKKy")
//                                    timberLog(dragEvent.y.toString(), "KKKKy")
//                                    true
//                                }
//
//                                DragEvent.ACTION_DRAG_EXITED -> {
//                                    true
//                                }
//                                DragEvent.ACTION_DROP -> {
//                                    true
//                                }
//                                DragEvent.ACTION_DRAG_ENDED -> {
//                                    timberLog(dragEvent.y.toString(), "KKKK")
//                                    timberLog("Ended", "KKKK")
//                                    true
//                                }
//                                else -> {
//                                    false
//                                }
//                            }
//                        }

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


    private fun addAllDayEvent(event: CalendarResponse.Event) {
        (inflater.inflate(
            R.layout.view_event_calendar,
            null,
            false
        ) as AppCompatTextView).apply {
            text = event.title
            setTextColor(Color.WHITE)
            binding.allEventContainer.addView(this)
            (layoutParams as LinearLayoutCompat.LayoutParams).apply {
                this.setMargins(4)
            }
            setOnClickListener {
                detailClickable(event)
            }
            val drawable = this.background as GradientDrawable
            drawable.setColor(Color.parseColor(requireContext().hexColor(event.type)))

        }
    }

    private fun getViewScaleDetector(): ScaleGestureDetector {
        return ScaleGestureDetector(requireContext(),
            object : ScaleGestureDetector.OnScaleGestureListener {
                override fun onScale(detector: ScaleGestureDetector?): Boolean {

                    if (detector != null) {
                        newRowHeight = rowHeight * detector.scaleFactor
                    }
                    val minHeight =
                        (getScreenHeight() - 60f.dpToPx() - (activity as HomeActivity).appBarSize() - 24f.dpToPx()) / 24 + 2
                    if (newRowHeight > minHeight && newRowHeight < maxHeight) {
                        rowHeight = newRowHeight
                        sharedVM.changeHeight(rowHeight)
                        updateViewScale()
                    }
                    return true
                }

                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    binding.weekEventsScrollview.isScrollable = false
                    isZooming = true
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                    binding.weekEventsScrollview.isScrollable = true
                    isZooming = false
                }

            })
    }


    private fun updateViewScale() {
        val fullHeight = max(rowHeight.toInt() * 24, binding.weekEventsScrollview.height)
        binding.dayView.layoutParams.height = fullHeight
        binding.dayView.setDayRowHeight(rowHeight)
        binding.eventContainer.layoutParams.height = fullHeight
        binding.weekEventsScrollview.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addHours()
        selectedGrid?.hide()
        storage.rowHeight = rowHeight
        addCurrentTimeIndicator()
        addEvents(events)
    }


    private fun createEvent(hour: Int) {
        val date = Formatter.getLocalDateTimeFromCode(dayCode).plusHours(hour).toString()
        sectionsDialog.showSectionsBottomSheetDialog(requireContext(), {
            sharedVM.clickedCreateEvent(
                R.id.action_navigation_calendar_to_navigation_create_note,
                date
            )
        }, {
            sharedVM.clickedCreateEvent(
                R.id.action_navigation_calendar_to_navigation_create_task,
                date
            )
        },
            {
                sharedVM.clickedCreateEvent(
                    R.id.action_navigation_calendar_to_navigation_create_meeting,
                    date
                )
            },
            {
                sharedVM.clickedCreateEvent(
                    R.id.action_navigation_calendar_to_navigation_create_dating,
                    date
                )
            })
    }

    private fun loadView() {
        val dateTime = Formatter.getDateTimeFromCode(dayCode)
        if (dayCode == Formatter.getTodayCode()) {
            isToday = true
        }
        if (isToday) {
            binding.tvToday.setTextColor(Color.WHITE)
            binding.mcvDay.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }


        rowHeight = if (storage.rowHeight == 0.0f) {
            resources.getDimension(R.dimen.week_view_height)
        } else {
            binding.dayView.setDayRowHeight(storage.rowHeight)
            storage.rowHeight
        }

        binding.tvToday.text = dateTime.dayOfMonth.toString()
        binding.tvDayOfWeek.text =
            resources.getStringArray(R.array.week_days_short)[dateTime.dayOfWeek - 1]

        binding.dayViewContainer.post {
            val topHeight = binding.dayViewContainer.height.toFloat().pxToDp()
            val bottomHeight = binding.dayViewContainer.width
            timberLog("$topHeight $bottomHeight", "KKKKL")

        }
        binding.weekEventsScrollview.requestDisallowInterceptTouchEvent(true)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGrid() {
        val gestureDetector = getViewGestureDetector(binding.dayViewContainer)
        binding.dayViewContainer.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
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
                        layoutParams.width = view.width

                        y = hour * rowHeight


                        setOnClickListener {
                            createEvent(hour)
                        }
                    }
                    return true
                }
            })
    }

    @SuppressLint("InflateParams")
    private fun addHours() {
        val heightHours = rowHeight
        binding.dayViewHourContainer.removeAllViews()
        for (i in 1..24) {
            var hours = "$i:00"
            if (i == 24) {
                hours = ""
            }
            (layoutInflater.inflate(
                R.layout.weekly_view_hour_textview,
                binding.dayViewHourContainer,
                false
            ) as AppCompatTextView).apply {
                text = hours
                height =
                    heightHours.toInt()

                binding.dayViewHourContainer.addView(this)
            }
        }
    }


    private fun addCurrentTimeIndicator() {
        if (isToday) {
            val calendar = Calendar.getInstance()
            val minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

            var minuteHeight: Float

            if (currentTimeView != null) {
                binding.dayViewContainer.removeView(currentTimeView)
            }

            currentTimeView = (layoutInflater.inflate(
                R.layout.calendar_now_marker,
                null,
                false
            ) as View).apply {
                binding.dayViewContainer.addView(this, 0)
                (layoutParams as RelativeLayout.LayoutParams).apply {
                    height = 2
                }
                x = 0f
                binding.dayViewContainer.onGlobalLayoutHeight { height, _ ->
                    minuteHeight = height / 60f / 24f
                    y = minutes * minuteHeight - 10f
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun itemClick(event: CalendarResponse.Event) {
        detailClickable(event)
    }

    fun updateScrollY(rowHeight: Float) {
        if (wasFragmentInit) {
            this.rowHeight = rowHeight
            updateViewScale()
        }
    }

    fun updateViewState(state: Int) {
        if (wasFragmentInit) {
            viewState = state
            checkViewState()
        }
    }


}



