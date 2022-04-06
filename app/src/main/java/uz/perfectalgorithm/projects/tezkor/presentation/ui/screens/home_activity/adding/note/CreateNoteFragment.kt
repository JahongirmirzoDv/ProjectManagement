package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NotePostRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateNoteBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.note.EventDeleteClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.note.NoteReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.ReminderNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.note.CreateNoteViewModel
import uz.perfectalgorithm.projects.tezkor.utils.adding.isInputCompleted
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

/***
 * Abduraxmonov Abdulla 11/09/2021
 * Bu eslatma uchun create qismi
 */

@AndroidEntryPoint
class CreateNoteFragment : BaseFragment(), EventDeleteClickListener, CoroutineScope {
    private var _binding: FragmentCreateNoteBinding? = null
    private val binding: FragmentCreateNoteBinding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    @Inject
    lateinit var credential: GoogleAccountCredential

    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar

    private val noteViewModel: CreateNoteViewModel by viewModels()
    private var startDateTime = DateTime()
    private var repeatMode = "Bir marta"
    private val listRepetitionServer = ArrayList<String>()
    private lateinit var reminderAdapter: NoteReminderAdapter
    private val reminderList = mutableSetOf<NoteReminder>()
    var untilDateTime = DateTime()
    private var repeatWeekRule = 0
    private var repeatMonthRule = 0
    private var reminderMin = 0
    private var exceptionDate = ""
    private var taskTitle: String? = null
    private var messageID: Int? = null
    private var startDate: String? = null
    private val job = Job()
    private val TAG = "CreateNoteFragment"


    @Inject
    lateinit var storage: LocalStorage
    private val getProgressData = Observer<Boolean> {
        if (it) {
            showLoadingDialog()
        } else {
            loadingDialog?.dismiss()
            loadingAskDialog?.dismiss()
        }
    }
    private val getError = Observer<Throwable> {
        loadingDialog?.dismiss()
        loadingAskDialog?.dismiss()
        timberLog(it.message.toString())
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val postNoteResponse = Observer<NoteData> {
        if (startDate != null) {
            credential.selectedAccountName = storage.googleCalendarAccountName
            syncGoogleCalendar()
        }
        makeDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskTitle = it.getString("titleTask")
            messageID = it.getInt("messageID")
        }
//        Log.e(TAG, "onCreate: ${taskTitle} - ${messageID}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(layoutInflater)
        hideAppBar()
        hideBottomMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDate = arguments?.getString(START_DATE, DateTime.now().toString())

        startDateTime = DateTime(startDate)
        initView()
        loadObservers()
        loadAction()
        loadTime()
        loadArgs()

    }

    private fun loadArgs() {
        if (taskTitle != null) {
            binding.etNoteTitle.setText(taskTitle)
        }
    }

//
//    @SuppressLint("SetTextI18n")
//    private fun loadData(noteData: NoteData) {
//        binding.noteName.text = getString(R.string.update_note)
//        val formatDate = DateTimeFormat.forPattern(Formatter.SIMPLE_TIME_PATTERN)
//        startDateTime = formatDate.parseDateTime(noteData.startTime)
//
//
//
//        if (!noteData.untilDate.isNullOrEmpty()) {
//            untilDateTime = formatDate.parseDateTime(noteData.untilDate)
//        }
//        binding.apply {
//            tvNoteDate.text = noteViewModel.noteDate
//            etNoteDescription.setText(noteData.description)
//            etNoteTitle.setText(noteData.title)
//        }
//        reminderList.addAll(getReminderDate(noteData.reminder as ArrayList<Int>))
//        reminderAdapter.submitList(reminderList.toMutableList())
//        binding.reminderDateText.text = if (noteData.untilDate.isNullOrEmpty()) {
//            getString(R.string.always)
//        } else {
//            noteData.untilDate
//        }
//
//        when (noteData.repeat) {
//            listRepetitionServer[0] -> {
//                repeatMode = resources.getStringArray(R.array.list_repeat)[0]
//                binding.repeatText.text = repeatMode
//                setRepeat(repeatMode)
//            }
//            listRepetitionServer[1] -> {
//                repeatMode = resources.getStringArray(R.array.list_repeat)[1]
//                binding.repeatText.text = repeatMode
//                setRepeat(repeatMode)
//            }
//            listRepetitionServer[2] -> {
//                repeatMode = resources.getStringArray(R.array.list_repeat)[2]
//                binding.repeatText.text = repeatMode
//                setRepeat(repeatMode)
//                repeatWeekText(noteData.repeatRule)
//            }
//            listRepetitionServer[3] -> {
//                repeatMode = resources.getStringArray(R.array.list_repeat)[3]
//                binding.repeatText.text = repeatMode
//                setRepeat(repeatMode)
//                repeatMonthYearText(noteData.repeatRule)
//            }
//            listRepetitionServer[4] -> {
//                repeatMode = resources.getStringArray(R.array.list_repeat)[4]
//                setRepeat(repeatMode)
//                binding.repeatText.text = repeatMode
//                repeatMonthYearText(noteData.repeatRule)
//            }
//        }
//    }

    private fun loadObservers() {
        noteViewModel.postNoteLiveData.observe(viewLifecycleOwner, postNoteResponse)
        noteViewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        noteViewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    private fun loadAction() {
        binding.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnNoteCreate.setOnClickListener {
            createNote()

        }
        binding.btnNoteCreate2.setOnClickListener {
            createNote()
        }
        binding.tvNoteDate.setOnClickListener {
            hideKeyboard()
            startDateAndTimePickerDialog()
        }
        binding.tpNoteTime.setIs24HourView(true)

        binding.tpNoteTime.setOnTimeChangedListener { _, hourOfDay, minute ->
            noteViewModel.noteTimeMinutes = minute
            noteViewModel.noteTimeHours = hourOfDay
        }
    }

    private fun loadTime() {
        noteViewModel.noteDate = startDateTime.toString(Formatter.SIMPLE_DAY_PATTERN)
        binding.tvNoteDate.text = noteViewModel.noteDate
        noteViewModel.noteTimeMinutes = startDateTime.minuteOfHour
        noteViewModel.noteTimeHours = startDateTime.hourOfDay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tpNoteTime.minute = startDateTime.minuteOfHour
            binding.tpNoteTime.hour = startDateTime.hourOfDay
        } else {
            binding.tpNoteTime.currentHour = noteViewModel.noteTimeHours
            binding.tpNoteTime.currentMinute = noteViewModel.noteTimeMinutes
        }
    }

    private fun createNote() {
        repeatMode = changeRepeatMode(binding.repeatText.text.toString())

        val isInputCompleted = isInputCompleted(
            listOf(
                Triple(
                    binding.etNoteTitle.text.isNullOrEmpty(),
                    binding.etNoteTitle,
                    getString(R.string.error_title_note)
                ),
                Triple(
                    binding.repeatText.text.isBlank(),
                    binding.repeatText,
                    getString(R.string.error_repeat)
                ),
                Triple(
                    (binding.reminderDateText.text != getString(
                        R.string.always
                    ) && repeatMode != listRepetitionServer[0]
                            ) && untilDateTime.isBefore(startDateTime),
                    binding.reminderDateTitle,
                    getString(R.string.error_until_date)
                ),
                Triple(
                    binding.repeatTypeText.text.isBlank() && repeatMode != listRepetitionServer[0] && repeatMode != listRepetitionServer[1],
                    binding.repeatTypeTitle,
                    getString(R.string.error_repeat_type)
                ),
                Triple(
                    noteViewModel.noteDate.isBlank(),
                    binding.tvNoteDate,
                    getString(R.string.error_note_date)
                )
            ), binding.scrollView

        )


//        if (binding.etNoteTitle.text.isNullOrEmpty()) {
//            binding.etNoteTitle.error = getString(R.string.error_title_note)
//            isInputCompleted = false
//        }
//        if (binding.repeatText.text.isBlank()) {
//            binding.repeatTitle.showTooltip(getString(R.string.error_repeat))
//            isInputCompleted = false
//        }
//
//        if (binding.reminderDateText.text != getString(
//                R.string.always
//            ) && repeatMode != listRepetitionServer[0]
//        ) {
//            if (untilDateTime.isBefore(startDateTime)) {
//                binding.reminderDateTitle.showTooltip(getString(R.string.error_until_date))
//                isInputCompleted = false
//            }
//        }
//        if (binding.repeatTypeText.text.isBlank() && repeatMode != listRepetitionServer[0] && repeatMode != listRepetitionServer[1]) {
//            binding.repeatTypeTitle.showTooltip(getString(R.string.error_repeat_type))
//            isInputCompleted = false
//        }
//        if (noteViewModel.noteDate.isBlank()) {
//            binding.tvNoteDate.showTooltip(getString(R.string.error_note_date))
//            isInputCompleted = false
//        }
//        if (binding.reminderText.text.isBlank()) {
//            binding.reminderTitle.showTooltip(getString(R.string.error_reminder_time))
//            isInputCompleted = false
//        }

        val startDateTime =
            "${noteViewModel.noteDate} ${
                String.format(
                    "%02d",
                    noteViewModel.noteTimeHours
                )
            }:${String.format("%02d", noteViewModel.noteTimeMinutes)}"

        if (isInputCompleted) {
            val noteRequest = NotePostRequest(
                startDateTime,
                if (messageID != null) messageID else null,
                repeatMode,
                binding.etNoteDescription.text.toString(),
                binding.etNoteTitle.text.toString(),
                if (repeatMode == listRepetitionServer[2]) {
                    repeatWeekRule
                } else {
                    repeatMonthRule
                },
                reminders = reminderList.asSequence().mapNotNull { it.minutesTime }
                    .toMutableList(),
                if (binding.repeatText.text != getString(R.string.once) && binding.reminderDateText.text != getString(
                        R.string.always
                    )
                ) untilDateTime.toString(
                    Formatter.SIMPLE_TIME_PATTERN
                ) else null
            )
            noteViewModel.postNote(noteRequest)
        }
    }

    private fun syncGoogleCalendar() {
        launch {

            val event: Event = Event()
                .setSummary(binding.etNoteTitle.text.toString())
                .setDescription(binding.etNoteDescription.text.toString() ?: "")

            val startDateTime = com.google.api.client.util.DateTime(
                "${noteViewModel.noteDate}T${
                    String.format(
                        "%02d",
                        noteViewModel.noteTimeHours
                    )
                }:${String.format("%02d", noteViewModel.noteTimeMinutes)}:00-07:00"
            )
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setStart(start)

            val endDateTime = com.google.api.client.util.DateTime(
                "${noteViewModel.noteDate}T${
                    String.format(
                        "%02d",
                        noteViewModel.noteTimeHours
                    )
                }:${String.format("%02d", noteViewModel.noteTimeMinutes)}:00-07:00"
            )
            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setEnd(end)

            val recurrence = arrayOf("RRULE:FREQ=DAILY", "COUNT=2")
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
        makeSuccessSnack("Muvaffaqqiyatli saqlandi")
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "taskSuccessful",
            true
        )
        findNavController().navigateUp()
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

    private fun startDateAndTimePickerDialog(
    ) {
//        val timePickerDialog = TimePickerDialog(
//            requireContext(),
//            R.style.DialogTheme,
//            { _, hour, minute ->
//                startDateTime = startDateTime.withHourOfDay(hour).withMinuteOfHour(minute)
//                binding.tvBeginTime.text = startDateTime.toString(Formatter.PATTERN_HOURS_24)
//            },
//            startDateTime.hourOfDay,
//            startDateTime.minuteOfHour,
//            true
//        )
//        timePickerDialog.updateTime(startDateTime.hourOfDay, startDateTime.minuteOfHour)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DialogTheme,
            { _, year, month, day ->
                startDateTime = startDateTime.withDate(year, month + 1, day)
                noteViewModel.noteDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                binding.tvNoteDate.text = noteViewModel.noteDate
//                timePickerDialog.show()
            },
            startDateTime.year,
            startDateTime.monthOfYear - 1,
            startDateTime.dayOfMonth
        )
        datePickerDialog.updateDate(
            startDateTime.yearOfEra,
            startDateTime.monthOfYear - 1,
            startDateTime.dayOfMonth
        )
        datePickerDialog.show()
    }


    private fun initView() {
        reminderAdapter = NoteReminderAdapter(requireContext(), this)
        binding.rvReminder.adapter = reminderAdapter
        binding.rvReminder.layoutManager = LinearLayoutManager(requireContext())

        listRepetitionServer.addAll(resources.getStringArray(R.array.list_repeat_server))

        binding.apply {
            repeatLayout.setOnClickListener {
                showRepeatNoteDialog(this@CreateNoteFragment::setRepeat)
            }

            repeatTypeLayout.setOnClickListener {
                val repeat = binding.repeatText.text.toString()
                showRepeatRule(repeat)
            }
            reminderDateLayout.setOnClickListener {

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    { _, hour, minute ->
                        untilDateTime = untilDateTime.withHourOfDay(hour).withMinuteOfHour(minute)
                        binding.reminderDateText.text =
                            untilDateTime.toString(Formatter.SIMPLE_TIME_PATTERN)
                    },
                    startDateTime.hourOfDay,
                    startDateTime.minuteOfHour,
                    true
                )
//                timePickerDialog.updateTime(startDateTime.hourOfDay, startDateTime.minuteOfHour)


                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    { _, year, month, day ->
                        untilDateTime =
                            untilDateTime.withDate(year, month + 1, day).withTime(0, 0, 0, 0)
                        binding.reminderDateText.text = String.format(
                            "%02d-%02d-%02d", year, month + 1, day
                        )
                        timePickerDialog.show()
                    },
                    startDateTime.year,
                    startDateTime.monthOfYear - 1,
                    startDateTime.dayOfMonth
                )

                datePickerDialog.updateDate(
                    untilDateTime.yearOfEra,
                    untilDateTime.monthOfYear - 1,
                    untilDateTime.dayOfMonth
                )
                datePickerDialog.show()
            }

            setReminder(Pair(getString(R.string.ab1), 0))
            reminderAdd.setOnClickListener {
                ReminderNoteDialog(requireContext(), this@CreateNoteFragment::setReminder).show()
            }
        }
    }

    private fun setReminder(pair: Pair<String, Int>) {
        reminderList.add(NoteReminder(pair.first, pair.second))
        reminderAdapter.submitList(reminderList.toMutableList())
    }

//    private fun getReminderDate(listReminderMinutes: ArrayList<Int>): List<NoteReminder> {
//
//        val listReminder = ArrayList<NoteReminder>()
//        listReminderMinutes.forEach {
//            when {
//                it == 0 -> {
//                    listReminder.add(NoteReminder("O'z vaqtida", it))
//                }
//                it in 1..59 -> {
//                    listReminder.add(NoteReminder("$it daqiqa oldin", it))
//                }
//                it >= 60 && it < 60 * 24 -> {
//                    listReminder.add(NoteReminder("${it / 60} soat oldin", it))
//                }
//                it >= 60 * 24 && it < 60 * 24 * 7 -> {
//                    listReminder.add(NoteReminder("${it / (60 * 24)} kun oldin", it))
//                }
//                it == 60 * 24 * 7 -> {
//                    listReminder.add(NoteReminder("1 hafta oldin", it))
//                }
//            }
//        }
//
//        return listReminder
//    }


    private fun showRepeatRule(repeat: String) {
        when (repeat) {
            getString(R.string.every_week) -> {
                RepeatRuleWeeklyDialog(requireContext(), repeatWeekRule) { it ->
                    repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(requireContext(), startDateTime, repeatMonthRule, true) { it ->
                    repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(requireContext(), startDateTime, repeatMonthRule, false) { it ->
                    repeatMonthRule = it
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

    private fun setRepeat(repeat: String) {
        binding.repeatText.text = repeat

        when (repeat) {
            getString(R.string.every_week) -> {
                repeatWeekRule = 2.0.pow(startDateTime.dayOfWeek.toDouble() - 1).toInt()
                repeatWeekText(repeatWeekRule)
            }
            getString(R.string.every_month) -> {
                repeatMonthRule = 1
                repeatMonthYearText(repeatMonthRule)
            }
            getString(R.string.every_year) -> {
                repeatMonthRule = 1
                repeatMonthYearText(repeatMonthRule)
            }
        }

        if (repeat != getString(R.string.once) && repeat != getString(R.string.every_day)) {
            binding.repeatTypeLayout.visible()
            binding.view222.visible()
        } else {
            binding.repeatTypeLayout.hide()
            binding.view222.hide()
        }
        if (repeat != getString(R.string.once)) {
            binding.reminderDateLayout.visible()
            binding.view224.visible()
        } else {
            binding.reminderDateLayout.hide()
            binding.view224.hide()
        }

        showRepeatRule(repeat)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteItem(event: NoteReminder) {
        reminderList.remove(event)
        reminderAdapter.submitList(reminderList.toMutableList())
    }

    override val coroutineContext: CoroutineContext
        get() = job
}
