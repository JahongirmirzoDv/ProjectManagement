package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.note

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.note.NoteReminder
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteDeleteOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditOnesRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.note.NoteEditRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.note.NoteEditOnesData
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDetailNoteBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.note.EventDeleteClickListener
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.note.NoteReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.DeleteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.*
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base.BaseFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.note.DetailNoteViewModel
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

/***
 * Bu eslatma uchun detail qismi
 */

@AndroidEntryPoint
class DetailNoteFragment : BaseFragment(), EventDeleteClickListener {
    private var _binding: FragmentDetailNoteBinding? = null
    private val binding: FragmentDetailNoteBinding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    private val noteViewModel: DetailNoteViewModel by viewModels()
    private var startDateTime = DateTime()
    private var repeatMode = ""
    private val listRepetitionServer = ArrayList<String>()
    private lateinit var reminderAdapter: NoteReminderAdapter
    private val lastReminderList = mutableSetOf<NoteReminder>()
    private val reminderList = mutableSetOf<NoteReminder>()
    private val createReminderList = mutableSetOf<Int>()
    private val deleteReminderList = mutableSetOf<Int>()
    var untilDateTime = DateTime()
    private var noteId = -1
    private var repeatWeekRule = 0
    private var repeatMonthRule = 0
    private var noteData: NoteData? = null
    private var exceptionDate = ""
    private var isEditorMode = false


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

    private val getNoteResponse = Observer<NoteData> { it ->
        noteData = it
        loadData(it)
    }

    private val getDeleteResponse = Observer<Boolean> {
        findNavController().navigateUp()
        makeSuccessSnack(getString(R.string.delete_successful))
    }

    private val postNoteResponse = Observer<NoteData> {
        makeSuccessSnack("Saqlandi...")
        findNavController().navigateUp()
    }

    private val editOnesResponse = Observer<NoteEditOnesData> {
        makeSuccessSnack("yangilandi...")
        findNavController().navigateUp()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailNoteBinding.inflate(layoutInflater)
        hideAppBar()
        hideBottomMenu()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteId = arguments?.getInt(EVENT_ID, -1) ?: -1
        exceptionDate =
            arguments?.getString(CURRENT_DATE)
                ?: DateTime.now().toString(Formatter.SIMPLE_DAY_PATTERN)

        initView()
        loadObservers()
        if (noteId != -1) {
            noteViewModel.getNote(noteId)
        }
        loadAction()
        disableEnableControls(isEditorMode, binding.scrollView)
    }


    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
            reminderAdapter.changeImage(enable)
        }
        binding.btnNoteDelete.isEnabled = true
    }


    @SuppressLint("SetTextI18n")
    private fun loadData(noteData: NoteData) {
        val formatDate = DateTimeFormat.forPattern(Formatter.SIMPLE_TIME_PATTERN)
        startDateTime = formatDate.parseDateTime(noteData.startTime)
        noteViewModel.oldTime = noteData.startTime

        if (!noteData.untilDate.isNullOrEmpty()) {
            untilDateTime = formatDate.parseDateTime(noteData.untilDate)
        }
        binding.apply {
            tvNoteDate.text = startDateTime.toString(Formatter.SIMPLE_DAY_PATTERN)
            etNoteDescription.setText(noteData.description)
            etNoteTitle.setText(noteData.title)
        }
        reminderList.addAll(getReminderDate(noteData.reminder as ArrayList<Int>))
        lastReminderList.addAll(reminderList)
        reminderAdapter.submitList(reminderList.toMutableList())
        binding.reminderDateText.text = if (noteData.untilDate.isNullOrEmpty()) {
            getString(R.string.always)
        } else {
            noteData.untilDate
        }
        loadTime()

        when (noteData.repeat) {
            listRepetitionServer[0] -> {
                repeatMode = resources.getStringArray(R.array.list_repeat)[0]
                binding.repeatText.text = repeatMode
                setRepeat(repeatMode)
            }
            listRepetitionServer[1] -> {
                repeatMode = resources.getStringArray(R.array.list_repeat)[1]
                binding.repeatText.text = repeatMode
                setRepeat(repeatMode)
            }
            listRepetitionServer[2] -> {
                repeatMode = resources.getStringArray(R.array.list_repeat)[2]
                repeatWeekRule = noteData.repeatRule
                binding.repeatText.text = repeatMode
                setRepeat(repeatMode)
                repeatWeekText(noteData.repeatRule)
            }
            listRepetitionServer[3] -> {
                repeatMode = resources.getStringArray(R.array.list_repeat)[3]
                binding.repeatText.text = repeatMode
                repeatMonthRule = noteData.repeatRule
                setRepeat(repeatMode)
                repeatMonthYearText(noteData.repeatRule)
            }
            listRepetitionServer[4] -> {
                repeatMode = resources.getStringArray(R.array.list_repeat)[4]
                repeatMonthRule = noteData.repeatRule
                setRepeat(repeatMode)
                binding.repeatText.text = repeatMode
                repeatMonthYearText(noteData.repeatRule)
            }
        }
    }

    private fun loadObservers() {
        noteViewModel.editOnesLiveData.observe(viewLifecycleOwner, editOnesResponse)
        noteViewModel.postNoteLiveData.observe(viewLifecycleOwner, postNoteResponse)
        noteViewModel.deleteLiveData.observe(viewLifecycleOwner, getDeleteResponse)
        noteViewModel.getNoteLiveData.observe(viewLifecycleOwner, getNoteResponse)
        noteViewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
        noteViewModel.errorLiveData.observe(viewLifecycleOwner, getError)
    }

    private fun loadAction() {
        binding.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnNoteCreate2.setOnClickListener {
            updateNote()
        }
        binding.tvNoteDate.setOnClickListener {
            if (isEditorMode) {
                hideKeyboard()
                startDateAndTimePickerDialog()
            }
        }
        binding.tpNoteTime.setIs24HourView(true)

        binding.tpNoteTime.setOnTimeChangedListener { _, hourOfDay, minute ->
            noteViewModel.noteTimeMinutes = minute
            noteViewModel.noteTimeHours = hourOfDay
        }
        binding.btnNoteEdit.setOnClickListener {
            if (isEditorMode) {
                updateNote()
            } else {
                binding.btnNoteEdit.setImageResource(R.drawable.ic_checedk_in_create_project)
                isEditorMode = true
                switchMode()
            }
        }
        binding.btnNoteDelete.setOnClickListener {
            val deleteDialog = DeleteDialog(requireContext())
            deleteDialog.show()
            deleteDialog.deleteClickListener {

                if (binding.repeatText.text.toString() != getString(R.string.once)) {
                    val askDialog = DeleteNoteAskDialog(requireContext()) {
                        if (it == 0) {
                            if (exceptionDate.isEmpty()) {
                                noteData?.id?.let { it1 ->
                                    noteViewModel.deleteNoteOnes(
                                        NoteDeleteOnesRequest(
                                            it1,
                                            DateTime.now().toString(Formatter.SIMPLE_DAY_PATTERN)
                                        )
                                    )
                                }
                            } else {
                                noteData?.id?.let { it1 ->
                                    noteViewModel.deleteNoteOnes(
                                        NoteDeleteOnesRequest(
                                            it1,
                                            DateTime(exceptionDate)
                                                .toString(Formatter.SIMPLE_DAY_PATTERN)
                                        )
                                    )
                                }
                            }

                        } else {
                            noteData?.id?.let { it1 -> noteViewModel.deleteNote(it1) }
                        }
                    }
                    askDialog.show()
                } else {
                    noteData?.id?.let { it1 -> noteViewModel.deleteNote(it1) }
                }

            }
        }

        binding.apply {
            etNoteTitle.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    findNavController().navigate(
                        R.id.action_detail_note_create_note,
                        bundleOf(
                            "note_data" to noteData,
                            CURRENT_EXCEPTION to DateTime(exceptionDate)
                                .toString(Formatter.SIMPLE_DAY_PATTERN)
                        )
                    )
                }
            })
        }
    }

    private fun switchMode() {
        disableEnableControls(isEditorMode, binding.scrollView)
        binding.apply {
            btnNoteDelete.isVisible = !isEditorMode
            btnNoteCreate2.isVisible = isEditorMode
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

    private fun updateNote() {
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

        timberLog(startDateTime,"RRRRLL")
        timberLog(noteViewModel.oldTime,"RRRRLL")

        if (isInputCompleted) {
            if (binding.repeatText.text != getString(R.string.once) && noteViewModel.oldTime != startDateTime) {
                val askDialog = EditNoteDialog(requireContext()) {
                    if (it == 0) {
                        val noteEditOnesRequest = NoteEditOnesRequest(
                            noteId,
                            DateTime(exceptionDate).toString(Formatter.SIMPLE_DAY_PATTERN),
                            startDateTime
                        )
                        noteViewModel.updateNoteOnes(noteEditOnesRequest)
                    } else {
                        updateNoteAll(startDateTime)
                    }
                }
                askDialog.show()
            } else {
                updateNoteAll(startDateTime)
            }
        }
    }

    private fun updateNoteAll(startDateTime: String) {
        createReminderList.removeAll(lastReminderList.asSequence().mapNotNull { it.minutesTime })

        val noteRequest = NoteEditRequest(
            startDateTime,
            repeatMode,
            binding.etNoteDescription.text.toString(),
            binding.etNoteTitle.text.toString(),
            if (repeatMode == listRepetitionServer[2]) {
                repeatWeekRule
            } else {
                repeatMonthRule
            },
            createReminderList.toMutableList(),
            deleteReminderList.toMutableList(),
            if (binding.repeatText.text != getString(R.string.once) && binding.reminderDateText.text != getString(
                    R.string.always
                )
            ) untilDateTime.toString(
                Formatter.SIMPLE_TIME_PATTERN
            ) else null
        )
        noteViewModel.updateNote(noteId, noteRequest)
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
                showRepeatNoteDialog(this@DetailNoteFragment::setRepeat)
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
                        untilDateTime =
                            untilDateTime.withHourOfDay(hour).withMinuteOfHour(minute)
                        binding.reminderDateText.text =
                            untilDateTime.toString(Formatter.SIMPLE_TIME_PATTERN)
                    },
                    startDateTime.hourOfDay,
                    startDateTime.minuteOfHour,
                    true
                )


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
            reminderAdd.setOnClickListener {
                ReminderNoteDialog(
                    requireContext(),
                    this@DetailNoteFragment::setReminder
                ).show()
            }
        }
    }

    private fun setReminder(pair: Pair<String, Int>) {
        reminderList.add(NoteReminder(pair.first, pair.second))
        createReminderList.add(pair.second)
        reminderAdapter.submitList(reminderList.toMutableList())
    }

    private fun getReminderDate(listReminderMinutes: ArrayList<Int>): List<NoteReminder> {

        val listReminder = ArrayList<NoteReminder>()
        listReminderMinutes.forEach {
            when {
                it == 0 -> {
                    listReminder.add(NoteReminder("O'z vaqtida", it))
                }
                it in 1..59 -> {
                    listReminder.add(NoteReminder("$it daqiqa oldin", it))
                }
                it >= 60 && it < 60 * 24 -> {
                    listReminder.add(NoteReminder("${it / 60} soat oldin", it))
                }
                it >= 60 * 24 && it < 60 * 24 * 7 -> {
                    listReminder.add(NoteReminder("${it / (60 * 24)} kun oldin", it))
                }
                it == 60 * 24 * 7 -> {
                    listReminder.add(NoteReminder("1 hafta oldin", it))
                }
            }
        }

        return listReminder
    }

    private fun showRepeatRule(repeat: String) {
        when (repeat) {
            getString(R.string.every_week) -> {
                timberLog(repeatWeekRule.toString(), "PPPPP1")
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
//                repeatWeekRule = 2.0.pow(startDateTime.dayOfWeek.toDouble() - 1).toInt()
                repeatWeekText(repeatWeekRule)
            }
            getString(R.string.every_month) -> {
//                repeatMonthRule = 1
                repeatMonthYearText(repeatMonthRule)
            }
            getString(R.string.every_year) -> {
//                repeatMonthRule = 1
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteItem(reminder: NoteReminder) {
        reminderList.remove(reminder)
        deleteReminderList.add(reminder.minutesTime)
        reminderAdapter.submitList(reminderList.toMutableList())
    }
}

//    private val noteViewModel: DetailNoteViewModel by viewModels()
//    private var noteData: NoteData? = null
//    var currentDate: String = ""
//
//    @Inject
//    lateinit var storage: LocalStorage
//    private val getProgressData = Observer<Boolean> {
//        if (it) {
//            showLoadingDialog()
//        } else {
//            loadingDialog?.dismiss()
//            loadingAskDialog?.dismiss()
//        }
//    }
//
//    private val getError = Observer<Throwable> { throwable ->
//        if (throwable is Exception) {
//            handleException(throwable)
//        } else {
//            makeErrorSnack(throwable.message.toString())
//        }
//    }
//
//    private val getNoteResponse = Observer<NoteData> { it ->
//        noteData = it
//        initData(it)
//    }
//
//    private val getDeleteResponse = Observer<Boolean> {
//        findNavController().navigateUp()
//        makeSuccessSnack(getString(R.string.delete_successful))
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentDetailNoteBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        hideAppBar()
//        val eventId = arguments?.getInt(EVENT_ID, -1)
//        currentDate =
//            arguments?.getString(CURRENT_DATE, Formatter.getDayCodeFromDateTime(DateTime.now()))
//                ?: Formatter.getDayCodeFromDateTime(DateTime.now())
//        loadAction()
//        loadObservers()
//
//        if (eventId != -1) {
//            noteViewModel.getNote(eventId!!)
//        }
//    }
//
//    private fun loadObservers() {
//        noteViewModel.deleteLiveData.observe(viewLifecycleOwner, getDeleteResponse)
//        noteViewModel.getNoteLiveData.observe(viewLifecycleOwner, getNoteResponse)
//        noteViewModel.progressLiveData.observe(viewLifecycleOwner, getProgressData)
//        noteViewModel.errorLiveData.observe(viewLifecycleOwner, getError)
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun loadAction() {
//        binding.ivBackButton.setOnClickListener {
//            findNavController().navigateUp()
//        }
//        binding.btnNoteEdit.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_detail_note_create_note,
//                bundleOf(
//                    "note_data" to noteData,
//                    CURRENT_EXCEPTION to DateTime(currentDate)
//                        .toString(Formatter.SIMPLE_DAY_PATTERN)
//                )
//            )
//        }
//        binding.btnNoteDelete.setOnClickListener {
//            val deleteDialog = DeleteDialog(requireContext())
//            deleteDialog.show()
//            deleteDialog.deleteClickListener {
//                if (binding.tvRepetition.text.toString() != getString(R.string.once)) {
//                    val askDialog = DeleteNoteAskDialog(requireContext()) {
//                        if (it == 0) {
//                            if (currentDate.isEmpty()) {
//                                noteData?.id?.let { it1 ->
//                                    noteViewModel.deleteNoteOnes(
//                                        NoteDeleteOnesRequest(
//                                            it1,
//                                            DateTime.now().toString(Formatter.SIMPLE_DAY_PATTERN)
//                                        )
//                                    )
//                                }
//                            } else {
//                                noteData?.id?.let { it1 ->
//                                    noteViewModel.deleteNoteOnes(
//                                        NoteDeleteOnesRequest(
//                                            it1,
//                                            DateTime(currentDate)
//                                                .toString(Formatter.SIMPLE_DAY_PATTERN)
//                                        )
//                                    )
//                                }
//                            }
//
//                        } else {
//                            noteData?.id?.let { it1 -> noteViewModel.deleteNote(it1) }
//                        }
//                    }
//                    askDialog.show()
//                } else {
//                    noteData?.id?.let { it1 -> noteViewModel.deleteNote(it1) }
//                }
//
//            }
//        }
//
//        binding.apply {
//            tvNoteTitle.setOnClickListener(object : DoubleClickListener() {
//                override fun onDoubleClick(v: View?) {
//                    findNavController().navigate(
//                        R.id.action_detail_note_create_note,
//                        bundleOf(
//                            "note_data" to noteData,
//                            CURRENT_EXCEPTION to DateTime(currentDate)
//                                .toString(Formatter.SIMPLE_DAY_PATTERN)
//                        )
//                    )
//                }
//            })
//        }
//    }
//
//    private fun initData(noteData: NoteData) {
//        val listRepetitionServer = resources.getStringArray(R.array.list_repeat_server)
//        val formatDate = DateTimeFormat.forPattern(Formatter.SIMPLE_TIME_PATTERN)
//        val startDateTime = formatDate.parseDateTime(noteData.startTime)
//        binding.apply {
//            tvBeginDate.text = startDateTime.toString(Formatter.SIMPLE_DAY_PATTERN)
//            tvBeginTime.text = startDateTime.toString(Formatter.PATTERN_HOURS_24)
//            tvNoteTitle.text = noteData.title
//            tvNoteDescription.text = noteData.description
//            tvReminder.text = noteData.reminder.toString()
//            tvUntilDate.text = if (noteData.untilDate.isNullOrEmpty()) {
//                getString(R.string.always)
//            } else {
//                noteData.untilDate
//            }
//        }
//
//        when (noteData.repeat) {
//            listRepetitionServer[0] -> {
//                binding.tvRepetition.text = resources.getStringArray(R.array.list_repeat)[0]
//                typeRepeat(false)
//                untilDate(false)
//            }
//            listRepetitionServer[1] -> {
//                binding.tvRepetition.text = resources.getStringArray(R.array.list_repeat)[1]
//                typeRepeat(false)
//                untilDate(true)
//            }
//            listRepetitionServer[2] -> {
//                binding.tvRepetition.text = resources.getStringArray(R.array.list_repeat)[2]
//                repeatWeekText(noteData.repeatRule)
//                typeRepeat(true)
//                untilDate(true)
//            }
//            listRepetitionServer[3] -> {
//                binding.tvRepetition.text = resources.getStringArray(R.array.list_repeat)[3]
//                repeatMonthYearText(noteData.repeatRule)
//                typeRepeat(true)
//                untilDate(true)
//            }
//            listRepetitionServer[4] -> {
//                binding.tvRepetition.text = resources.getStringArray(R.array.list_repeat)[4]
//                repeatMonthYearText(noteData.repeatRule)
//                typeRepeat(true)
//                untilDate(true)
//            }
//        }
//    }
//
//    private fun untilDate(b: Boolean) {
//        binding.apply {
//            if (b) {
//                tvUntilDateName.visible()
//                layoutUntilDate.visible()
//            } else {
//                tvUntilDateName.hide()
//                layoutUntilDate.hide()
//            }
//        }
//    }
//
//    private fun typeRepeat(b: Boolean) {
//        binding.apply {
//            if (b) {
//                tvRepeatTypeTitle.visible()
//                layoutRepeatTitle.visible()
//            } else {
//                tvRepeatTypeTitle.hide()
//                layoutRepeatTitle.hide()
//            }
//        }
//    }
//
//    private fun repeatWeekText(it: Int) {
//        when (it) {
//            WEEKEND_DAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.weekend_day)
//            }
//            WORK_DAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.work_day)
//            }
//            EVERY_DAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.every_day)
//            }
//            else -> {
//                binding.tvTypeRepeat.text = getSelectedDaysString(it)
//            }
//
//        }
//    }
//
//    private fun getSelectedDaysString(sumRepeat: Int): String {
//        val dayBits = arrayListOf(
//            MONDAY_SUM, TUESDAY_SUM, WEDNESDAY_SUM, THURSDAY_SUM, FRIDAY_SUM,
//            SATURDAY_SUM, SUNDAY_SUM
//        )
//        val weekDays = resources.getStringArray(R.array.week_days_short)
//            .toList() as java.util.ArrayList<String>
//
//        var days = ""
//        dayBits.forEachIndexed { index, it ->
//            if (sumRepeat and it != 0) {
//                days += "${weekDays[index]}, "
//            }
//        }
//        return days.trim().trimEnd(',')
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun repeatMonthYearText(it: Int) {
//        when (it) {
//            REPEAT_SAME_DAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.repeat_type_by_day)
//            }
//            REPEAT_ORDER_WEEKDAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.repeat_type_by_dayofweek)
//            }
//            REPEAT_ORDER_WEEKDAY_USE_LAST -> {
//                binding.tvTypeRepeat.text = getString(R.string.repeat_type_by_end_week)
//            }
//            REPEAT_LAST_DAY -> {
//                binding.tvTypeRepeat.text = getString(R.string.repeat_type_by_end)
//            }
//        }
//    }
//}
//
abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}