package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_meeting

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.meeting.CreateMeetingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.meeting.DiscussedTopic
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.toFilesItemList
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateMeetingBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.ReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.meeting.DiscussedTopicAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.tasks.projects.UserDataAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ModeratorDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.ReminderNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_meeting.CreateMeetingViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

/**
 * Meeting (majlis) yaratish ekrani
 * TODO:
 * 1. Qatnashuvchilar orasidan moderator tayinlash ni qo'shish kerak
 * 2. Kommentlar qo'shish (task dan olish mumkin)
 * 3. Start-end buttonni figmaga moslash
 * 4. Korib chiqilgan masala (discussed_topic) ni proyekt/vazifaga convert qilish
 * logikasini o'zgartirish.
 * Hozir shunchaki create screenga o'tkazib yuborilyapti.
 * Figmadagi holatga keltirish kerak
 */
@AndroidEntryPoint
class
CreateMeetingFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentCreateMeetingBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val viewModel by viewModels<CreateMeetingViewModel>()
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val meetingDataHolder by lazy { AddingDataHolder() }
    private val navController by lazy { findNavController() }
    private val listRepetitionServer by lazy { resources.getStringArray(R.array.list_repeat_server) }
    private val dateTimeFormatter by lazy { DateTimeFormat.forPattern("yyyy-MM-dd HH:mm") }
    private val discussedTopicAdapter by lazy { DiscussedTopicAdapter(::onDiscussedTopicMoreClick) }
    private val reminderAdapter by lazy { ReminderAdapter(::onDeleteReminder, true) }
    private val participantsAdapter by lazy {
        UserDataAdapter(::onDeleteParticipant).apply {
            isEditorMode = true
        }
    }
    var taskTitle: String? = null
    var messageID: Int? = null
    private var startDate: String? = null
    private val job = Job()

    @Inject
    lateinit var storage: LocalStorage
    @Inject
    lateinit var credential: GoogleAccountCredential
    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskTitle = it.getString("titleTask")
            messageID = it.getInt("messageID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMeetingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()
        setupObservers()
        setupViews()
        setupClickListeners()
        loadArgs()
    }

    private fun setupViews() = with(binding) {
        if (meetingDataHolder.repeatString != null && meetingDataHolder.repeatString != getString(R.string.once) && meetingDataHolder.repeatString != getString(
                R.string.every_day
            )
        ) {
            binding.repeatTypeLayout.visible()
        } else {
            binding.repeatTypeLayout.hide()
        }
        if (meetingDataHolder.repeatString != null && meetingDataHolder.repeatString != getString(R.string.once)) {
            binding.reminderDateLayout.visible()
        } else {
            binding.reminderDateLayout.hide()
        }
        ivAuthor.loadImageUrl(storage.userImage, R.drawable.ic_person)
        startDate = arguments?.getString(START_DATE)
        if (startDate != null) {
            val startDateTime = DateTime(startDate)
            val endDateTime = DateTime(startDate).plusHours(1)
            meetingDataHolder.startDate = startDateTime.toLocalDate()
            meetingDataHolder.startTime = startDateTime.toLocalTime()
            meetingDataHolder.endDate = endDateTime.toLocalDate()
            meetingDataHolder.endTime = endDateTime.toLocalTime()
        }
        rvParticipants.adapter = participantsAdapter
        rvReminders.adapter = reminderAdapter
        rvProblems.adapter = discussedTopicAdapter
        with(meetingDataHolder) {
            startTime?.let { setStartTime(it) }
            startDate?.let { setStartDate(it) }
            endTime?.let { setEndTime(it) }
            endDate?.let { setEndDate(it) }
            reminderDate?.let { setReminderDate(it) }
            files?.let { tvFilesCount.text = it.size.toString() }
            importance?.let { setImportance(it) }
            repeatString?.let { setRepeat(it) }
            reminderDate?.let { setReminderDate(it) }
            participants?.let { setParticipants(it) }
            reminders?.let { reminderAdapter.submitList(it.toMutableList()) }
        }
        if (meetingDataHolder.importance == null) {
            setImportance(Pair("green", "Past"))
        }
    }

    private fun loadArgs() {
        if (taskTitle != null) {
            binding.etMeetingTitle.setText(taskTitle)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() = with(binding) {
        createMeetingBlueButton.setOnClickListener {
            hideKeyboard()
            createMeeting()
        }

        if (meetingDataHolder.canEditTime == null) {
            meetingDataHolder.canEditTime = true
        }
        ivPinDateTime.setOnClickListener {
            showEditRuleDialog(meetingDataHolder.canEditTime ?: true) {
                meetingDataHolder.canEditTime = it
                if (it) {
                    ivPinDateTime.rotation = 0f
                } else {
                    ivPinDateTime.rotation = -45f
                }
            }
        }

        btnCreateMeeting.setOnClickListener {
            hideKeyboard()
            createMeeting()
        }

        imgBackButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }

        participantsLayout.setDropDownClick(
            requireContext(),
            binding.rvParticipants,
            binding.ivArrowParticipants
        )

        problemsLayout.setDropDownClick(
            requireContext(),
            rvProblems,
            null
        )

        reminderLayout.setDropDownClick(
            requireContext(),
            rvReminders,
            null
        )

        cvImportance.setOnClickListener {
            showImportanceDialog(this@CreateMeetingFragment::setImportance)
        }

        ivAddParticipant.setOnClickListener {
            storage.participant = PARTICIPANTS
            if (meetingDataHolder.participants == null) {
                storage.persons = emptySet()
            } else {
                storage.persons = meetingDataHolder.participants!!.map { it.id.toString() }.toSet()
            }
            showSelectPersonFragment(R.string.select_participants.toString())
        }

        cvFiles.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(
                CreateMeetingFragmentDirections.toSelectedFilesFragment(
                    meetingDataHolder.files?.toFilesItemList()?.toTypedArray()
                        ?: emptyArray(), true
                )
            )
        }

        getDateTime()
        startDateTimeLayout.setOnClickListener {
            showDateTimePickerDialog(
                this@CreateMeetingFragment::setStartTime,
                this@CreateMeetingFragment::setStartDate
            )
        }

        endDateTimeLayout.setOnClickListener {
            showDateTimePickerDialog(
                this@CreateMeetingFragment::setEndTime,
                this@CreateMeetingFragment::setEndDate
            )
        }

        tvStartTime.setOnClickListener {
            showTimePickerDialog(this@CreateMeetingFragment::setStartTime)
        }

        tvEndTime.setOnClickListener {
            showTimePickerDialog(this@CreateMeetingFragment::setEndTime)
        }

        tvStartDate.setOnClickListener {
            showDatePickerDialog(onDateSelected = this@CreateMeetingFragment::setStartDate)
        }

        tvEndDate.setOnClickListener {
            showDatePickerDialog(onDateSelected = this@CreateMeetingFragment::setEndDate)
        }

        if (meetingDataHolder.repeatString == null) {
            setRepeat("Bir marta")
        }
        repeatLayout.setOnClickListener {
            showRepeatNoteDialog(this@CreateMeetingFragment::setRepeat)
        }

        repeatTypeLayout.setOnClickListener {
            val repeat = binding.tvRepeatText.text.toString()
            showRepeatRule(repeat)
        }

        reminderDateLayout.setOnClickListener {
            showReminderDateDialog(
                if (tvReminderDateText.text.isNullOrBlank()) null else
                    LocalDateTime.parse(tvReminderDateText.text.toString(), dateTimeFormatter),
                this@CreateMeetingFragment::setReminderDate
            )
        }

        ivAddProblem.setOnClickListener {
            if (llCreateDiscussedTopic.isVisible) {
                etDiscussedTopic.setText("")
                llCreateDiscussedTopic.isVisible = false
                ivAddProblem.rotation = 0f
                hideKeyboard()
            } else {
                llCreateDiscussedTopic.isVisible = true
                ivAddProblem.rotation = 45f
                etDiscussedTopic.requestFocus()
            }
        }
        ivAddComment.setOnClickListener {
            if (llCreateComment.isVisible) {
                etComment.setText("")
                llCreateComment.isVisible = false
                ivAddComment.rotation = 0f
                hideKeyboard()
            } else {
                llCreateComment.isVisible = true
                ivAddComment.rotation = 45f
                etComment.requestFocus()
            }
        }

        if (meetingDataHolder.reminders == null) {
            meetingDataHolder.reminders = mutableSetOf()
            addReminder(Pair("O'z vaqtida", 0))
        }
        ivAddReminder.setOnClickListener {
            ReminderNoteDialog(
                requireContext(), this@CreateMeetingFragment::addReminder,
            ).show()
        }

        ivCreateDiscussedTopic.setOnClickListener {
            if (etDiscussedTopic.text.isNullOrBlank()) {
                etDiscussedTopic.error = "Reja matnini kiriting"
            } else {
                if (meetingDataHolder.discussedTopics == null) {
                    meetingDataHolder.discussedTopics = mutableListOf()
                }
                meetingDataHolder.discussedTopics?.add(
                    DiscussedTopic(
                        meetingDataHolder.discussedTopics?.size ?: 0,
                        etDiscussedTopic.text.toString()
                    )
                )
                discussedTopicAdapter.submitList(meetingDataHolder.discussedTopics?.toMutableList())
                tvProblemsCount.text = meetingDataHolder.discussedTopics?.size?.toString() ?: "0"
                etDiscussedTopic.setText("")
                llCreateDiscussedTopic.isVisible = false
                ivAddProblem.rotation = 0f
                hideKeyboard()
            }
        }
        ivCreateComment.setOnClickListener {
            if (etComment.text.isNullOrBlank()) {
                etComment.error = "Reja matnini kiriting"
            }else{
//                viewModel.createMeetingComment()
            }
        }

        clickOutside.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (llCreateDiscussedTopic.isVisible) {
                    val outRect = Rect()
                    val outExceptionRect = Rect()
                    llCreateDiscussedTopic.getGlobalVisibleRect(outRect)
                    ivAddProblem.getGlobalVisibleRect(outExceptionRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                        !outExceptionRect.contains(event.rawX.toInt(), event.rawY.toInt())
                    ) {
                        etDiscussedTopic.setText("")
                        hideKeyboard()
                        llCreateDiscussedTopic.isVisible = false
                        ivAddProblem.rotation = 0f
                    }
                }
            }
            false
        }

        participantsAdapter.listener = object : UserDataAdapter.Listener {
            override fun itemLongClick(data: PersonData,userId:Int, isModerator: Boolean, position: Int) {
                ModeratorDialog.showModeratorBottomSheetDialog(requireContext(), isModerator) {
                    if (isModerator) participantsAdapter.changeModerator(userId, position,false)
                    else participantsAdapter.changeModerator(data.id, position,true)
                }
            }

            override fun onItemClick(userId: Int, position: Int, isChecked: Boolean) {

            }
        }
    }

    private fun setupObservers() = with(viewModel) {
        createResponse.simpleCollect(
            this@CreateMeetingFragment,
            binding.progressLayout.progressLoader
        ) {
            if (startDate != null) {
                credential.selectedAccountName = storage.googleCalendarAccountName
                syncGoogleCalendar()
            }
            binding.progressLayout.progressLoader.isVisible = false
            sharedViewModel.setMeetingNeedsRefresh(true)
            makeDestroy()
        }

        navController.currentBackStackEntry?.savedStateHandle?.let {
            it.getLiveData<List<PersonData>?>(PARTICIPANTS)
                .observe(viewLifecycleOwner, this@CreateMeetingFragment::setParticipants)

            it.getLiveData<List<File>?>(NEW_FILES).observe(viewLifecycleOwner) {
                if (meetingDataHolder.files == null) {
                    meetingDataHolder.files = mutableListOf()
                }
                meetingDataHolder.files?.clear()
                meetingDataHolder.files?.addAll(it)
                binding.tvFilesCount.text = "${meetingDataHolder.files?.size ?: 0}"
            }
        }
    }

    private fun createMeeting() = with(binding) {
        val isInputCompleted = isInputCompleted(
            listOf(
                Triple(
                    etMeetingTitle.text.isNullOrBlank(),
                    etMeetingTitle,
                    getString(R.string.error_meeting_title)
                ),
                Triple(
                    etAddress.text.isNullOrBlank(),
                    etAddress,
                    getString(R.string.error_address)
                ),
                Triple(
                    meetingDataHolder.participants == null,
                    tvTitleParticipants,
                    getString(R.string.error_participants)
                ),
                Triple(
                    tvStartDate.text.isNullOrBlank() || tvStartTime.text.isNullOrBlank(),
                    tvTitleStartDateTime,
                    getString(R.string.error_start_date_time)
                ),
                Triple(
                    tvEndDate.text.isNullOrBlank() || tvEndTime.text.isNullOrBlank(),
                    tvTitleEndDateTime,
                    getString(R.string.error_end_date_time)
                ),
                Triple(
                    binding.tvRepeatText.text.isBlank(),
                    binding.tvRepeatText,
                    getString(R.string.error_repeat)
                ),
                Triple(
                    binding.repeatTypeText.text.isBlank() && meetingDataHolder.repeatString != listRepetitionServer[0] && meetingDataHolder.repeatString != listRepetitionServer[1] && meetingDataHolder.repeatString != null,
                    binding.repeatTypeTitle,
                    getString(R.string.error_repeat_type)
                ),
            ), nsvRoot
        )

        if (isInputCompleted) {
            viewModel.createMeeting(
                CreateMeetingRequest(
                    etMeetingTitle.text.toString(),
                    if (messageID != null) messageID else null,
                    etAddress.text.toString(),
                    meetingDataHolder.startDate?.toUiDate() + " " + meetingDataHolder.startTime?.toUiTime(),
                    meetingDataHolder.endDate?.toUiDate() + " " + meetingDataHolder.endTime?.toUiTime(),
                    meetingDataHolder.files,
                    meetingDataHolder.importance!!.first,
//                    meetingDataHolder.reminder?.first,
                    meetingDataHolder.reminders?.map { it.minutesTime } ?: emptyList(),
                    meetingDataHolder.participants?.map { it.id } ?: emptyList(),
                    if (meetingDataHolder.repeatString == "once") null else meetingDataHolder.reminderDate?.toUiDateTime(),
                    meetingDataHolder.repeatString ?: "once",
                    meetingDataHolder.discussedTopics?.map { it.title } ?: emptyList(),
                    if (meetingDataHolder.repeatString == listRepetitionServer[2]) {
                        meetingDataHolder.repeatWeekRule
                    } else {
                        meetingDataHolder.repeatMonthRule
                    },
                    meetingDataHolder.canEditTime ?: false,
                    binding.etDescription.text?.toString(),
                    participantsAdapter.moderatorId
                ))
        }
    }

    private fun syncGoogleCalendar() {
        launch {

            val event: Event = Event()
                .setSummary("${binding.etMeetingTitle.text.toString()}")
                .setLocation("${binding.etAddress.text.toString()}")
                .setDescription("${binding.etDescription.text.toString()}")

            val startDateTime = com.google.api.client.util.DateTime("${meetingDataHolder.startDate?.toUiDate()}T${meetingDataHolder.startTime?.toUiTime()}:00-07:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setStart(start)

            val endDateTime = com.google.api.client.util.DateTime("${meetingDataHolder.endDate?.toUiDate()}T${meetingDataHolder.endTime?.toUiTime()}:00-07:00")
            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setEnd(end)

            val recurrence = arrayOf("RRULE:FREQ=DAILY","COUNT=2")
            event.recurrence = listOf(recurrence) as MutableList<String>

            val arrayList = ArrayList<EventAttendee>()
            arrayList.add(EventAttendee().setEmail(storage.calendarID))
            event.attendees = arrayList

            val remindersOverrides = ArrayList<EventReminder>()
            remindersOverrides.add(EventReminder().setMethod("email").setMinutes(24 * 60))
            remindersOverrides.add(EventReminder().setMethod("popup").setMinutes(10))

            val reminders = Event.Reminders().setUseDefault(false).setOverrides(remindersOverrides)
            event.reminders = reminders


            try {

                val e = client.events().insert("${storage.calendarID}", event).execute()
                // Do whatever you want with the Drive service
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, 1)
            }
        }
    }

    private fun makeDestroy() {
        makeSuccessSnack("Muvaffaqqiyatli yaratildi")
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "taskSuccessful",
            true
        )
        findNavController().navigateUp()
    }

    private fun setStartTime(time: LocalTime) {
        time.isStartTimeValid(meetingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvStartTime.text = time.toUiTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setEndTime(time: LocalTime) {
        time.isEndTimeValid(meetingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvEndTime.text = time.toUiTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setStartDate(date: LocalDate) {
        date.isStartDateValid(meetingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvStartDate.text = date.toUiDate()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setEndDate(date: LocalDate) {
        date.isEndDateValid(meetingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvEndDate.text = date.toUiDate()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setReminderDate(dateTime: LocalDateTime) {
        dateTime.isReminderDateValid(meetingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvReminderDateText.text = dateTime.toUiDateTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun addReminder(pair: Pair<String, Int>) {
        val reminder = NoteReminder(pair.first, pair.second)
        meetingDataHolder.reminders?.add(reminder)
        reminderAdapter.submitList(meetingDataHolder.reminders?.toMutableList())
    }

    private fun getSelectedDaysString(sumRepeat: Int): String {
        val dayBits = arrayListOf(
            MONDAY_SUM, TUESDAY_SUM, WEDNESDAY_SUM, THURSDAY_SUM, FRIDAY_SUM,
            SATURDAY_SUM, SUNDAY_SUM
        )
        val weekDays = resources.getStringArray(R.array.week_days_short)
            .toList() as java.util.ArrayList<String>

        var days = ""
        dayBits.forEachIndexed { index, it ->
            if (sumRepeat and it != 0) {
                days += "${weekDays[index]}, "
            }
        }
        return days.trim().trimEnd(',')
    }

    private fun changeRepeatMode(repeat: String): String {
        return when (repeat) {
            getString(R.string.once) -> {
                listRepetitionServer[0]
            }
            getString(R.string.every_day) -> {
                listRepetitionServer[1]
            }
            getString(R.string.every_week) -> {
                listRepetitionServer[2]
            }
            getString(R.string.every_month) -> {
                listRepetitionServer[3]
            }
            else -> {
                listRepetitionServer[4]
            }
        }
    }

    private fun setRepeat(repeat: String) {
        binding.tvRepeatText.text = repeat
        meetingDataHolder.repeatString = changeRepeatMode(repeat)

        when (repeat) {
            getString(R.string.every_week) -> {
                meetingDataHolder.repeatWeekRule =
                    2.0.pow((meetingDataHolder.startDate ?: LocalDate()).dayOfWeek.toDouble() - 1)
                        .toInt()
                repeatWeekText(meetingDataHolder.repeatWeekRule)
            }
            getString(R.string.every_month) -> {
                meetingDataHolder.repeatMonthRule = 1
                repeatMonthYearText(meetingDataHolder.repeatMonthRule)
            }
            getString(R.string.every_year) -> {
                meetingDataHolder.repeatMonthRule = 1
                repeatMonthYearText(meetingDataHolder.repeatMonthRule)
            }
        }

        if (repeat != getString(R.string.once) && repeat != getString(R.string.every_day)) {
            binding.repeatTypeLayout.visible()
        } else {
            binding.repeatTypeLayout.hide()
        }
        if (repeat != getString(R.string.once)) {
            binding.reminderDateLayout.visible()
        } else {
            binding.reminderDateLayout.hide()
        }

        showRepeatRule(repeat)
    }

    private fun showRepeatRule(repeat: String) {
        when (repeat) {
            getString(R.string.every_week) -> {
                RepeatRuleWeeklyDialog(requireContext(), meetingDataHolder.repeatWeekRule) { it ->
                    meetingDataHolder.repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(
                    requireContext(),
                    meetingDataHolder.startDate?.toDateTime(LocalTime()) ?: DateTime(),
                    meetingDataHolder.repeatMonthRule,
                    true
                ) { it ->
                    meetingDataHolder.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(
                    requireContext(),
                    meetingDataHolder.startDate?.toDateTime(LocalTime()) ?: DateTime(),
                    meetingDataHolder.repeatMonthRule,
                    false
                ) { it ->
                    meetingDataHolder.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
        }
    }

    private fun repeatMonthYearText(it: Int) {
        when (it) {
            REPEAT_SAME_DAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_day)
            }
            REPEAT_ORDER_WEEKDAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_dayofweek)
            }
            REPEAT_ORDER_WEEKDAY_USE_LAST -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_end_week)
            }
            REPEAT_LAST_DAY -> {
                binding.repeatTypeText.text = getString(R.string.repeat_type_by_end)
            }
        }
    }

    private fun repeatWeekText(it: Int) {
        when (it) {
            WEEKEND_DAY -> {
                binding.repeatTypeText.text = getString(R.string.weekend_day)
            }
            WORK_DAY -> {
                binding.repeatTypeText.text = getString(R.string.work_day)
            }
            EVERY_DAY -> {
                binding.repeatTypeText.text = getString(R.string.every_day)
            }
            else -> {
                binding.repeatTypeText.text = getSelectedDaysString(it)
            }

        }
    }

    private fun setImportance(pair: Pair<String, String>) {
        meetingDataHolder.importance = pair
        binding.ivImportance.setImageResource(
            when (pair.first) {
                "green" -> R.drawable.ic_file_text_green
                "yellow" -> R.drawable.ic_file_text_yellow
                "red" -> R.drawable.ic_file_text_red
                else -> 0
            }
        )
    }

    private fun getDateTime() {
        binding.apply {
            meetingDataHolder.startDate = LocalDate(System.currentTimeMillis())
            meetingDataHolder.endDate = LocalDate(System.currentTimeMillis()+3600*1000)
            meetingDataHolder.startTime = LocalTime(System.currentTimeMillis())
            meetingDataHolder.endTime = LocalTime(System.currentTimeMillis()+3600*1000)

            tvStartDate.text = meetingDataHolder.startDate.toString()
            tvEndDate.text = meetingDataHolder.endDate.toString()
            tvStartTime.text = meetingDataHolder.startTime.toString().substring(0, 5)
            tvEndTime.text = meetingDataHolder.endTime.toString().substring(0, 5)
        }
    }

    private fun setParticipants(participants: List<PersonData>?) {
        meetingDataHolder.participants = participants?.toMutableList()
        participantsAdapter.submitList(participants)
    }

    private fun onDeleteParticipant(person: PersonData) {
        val newParticipants = meetingDataHolder.participants?.toMutableList().also {
            it?.remove(person)
        }
        newParticipants?.let { setParticipants(it) }
    }

    private fun onDeleteReminder(reminder: NoteReminder) {
        if (meetingDataHolder.reminders == null) {
            meetingDataHolder.reminders = mutableSetOf()
        }
        meetingDataHolder.reminders?.remove(reminder)
        reminderAdapter.submitList(meetingDataHolder.reminders?.toMutableList())
    }

    private fun onDeleteDiscussedTopic(topic: DiscussedTopic) {
        meetingDataHolder.discussedTopics?.remove(topic)
        discussedTopicAdapter.submitList(meetingDataHolder.discussedTopics?.toMutableList())
        binding.tvProblemsCount.text = meetingDataHolder.discussedTopics?.size?.toString() ?: "0"
    }

    private fun onDiscussedTopicMoreClick(topic: DiscussedTopic) {
        showConvertToDialog(
            {
                findNavController().navigate(
                    R.id.createMeetingFragment_to_createProjectFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title
                    )
                )
            },
            {
                findNavController().navigate(
                    R.id.createMeetingFragment_to_createTaskFragment,
                    bundleOf(
                        CONVERTED_TITLE to topic.title
                    )
                )
            },
            true,
            {
                onDeleteDiscussedTopic(topic)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = job
}