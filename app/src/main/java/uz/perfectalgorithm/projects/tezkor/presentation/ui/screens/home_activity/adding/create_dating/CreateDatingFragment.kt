package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.adding.create_dating

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
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
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.request.dating.CreateDatingRequest
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.task.toFilesItemList
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCreateDatingBinding
import uz.perfectalgorithm.projects.tezkor.databinding.LayoutDatingPartnerBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.detail_update.ReminderAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RadioGroupDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.ReminderNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.RepeatRuleWeeklyDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note.showRepeatNoteDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_dating.CreateDatingViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks.AddingSharedViewModel
import uz.perfectalgorithm.projects.tezkor.utils.*
import uz.perfectalgorithm.projects.tezkor.utils.adding.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeSuccessSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.*
import uz.perfectalgorithm.projects.tezkor.utils.flow.simpleCollect
import uz.perfectalgorithm.projects.tezkor.utils.views.setDropDownClick
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

/**
 *Created by farrukh_kh on 7/24/21 4:54 PM
 *uz.rdo.projects.projectmanagement.presentation.ui.screens.home_activity.adding.create_dating
 **/
/**
 * Dating (uchrashuv) yaratish oynasi
 * TODO:
 * 1. Figmadagi kabi ishtirokchilarni qo'shish kerak
 * (Kim bilan ni tagiga) (partner_in o'rniga participants)
 * 2. Figmadagi kabi status qo'shish kerak
 */
@AndroidEntryPoint
class CreateDatingFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentCreateDatingBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))

    private val viewModel by viewModels<CreateDatingViewModel>()

    //dating create bolgandan keyin, list ni update qilishni bildirish uchun (DatingsFragment)
    private val sharedViewModel by activityViewModels<AddingSharedViewModel>()
    private val datingDataHolder by lazy { AddingDataHolder() }
    private val navController by lazy { findNavController() }
    private val listRepetitionServer by lazy { resources.getStringArray(R.array.list_repeat_server) }
    private val dateTimeFormatter by lazy { DateTimeFormat.forPattern(BACKEND_DATE_TIME_FORMAT) }
    private val reminderAdapter by lazy { ReminderAdapter(::onDeleteReminder, true) }

    @Inject
    lateinit var storage: LocalStorage
    @Inject
    lateinit var credential: GoogleAccountCredential
    @Inject
    lateinit var client: com.google.api.services.calendar.Calendar

    private var startDate: String? = null
    private val job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDatingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideAppBar()
        hideBottomMenu()

        setupObservers()
        setupClickListeners()
        setupViews()
    }

    private fun setupViews() = with(binding) {
        if (datingDataHolder.repeatString != null &&
            datingDataHolder.repeatString != getString(R.string.once) &&
            datingDataHolder.repeatString != getString(
                R.string.every_day
            )
        ) {
            binding.repeatTypeLayout.visible()
        } else {
            binding.repeatTypeLayout.hide()
        }
        if (datingDataHolder.repeatString != null && datingDataHolder.repeatString != getString(R.string.once)) {
            binding.reminderDateLayout.visible()
        } else {
            binding.reminderDateLayout.hide()
        }
        ivAuthor.loadImageUrl(storage.userImage, R.drawable.ic_person)
        startDate = arguments?.getString(START_DATE)
        if (startDate != null) {
            val startDateTime = DateTime(startDate)
            val endDateTime = DateTime(startDate).plusHours(1)
            datingDataHolder.startDate = startDateTime.toLocalDate()
            datingDataHolder.startTime = startDateTime.toLocalTime()
            datingDataHolder.endDate = endDateTime.toLocalDate()
            datingDataHolder.endTime = endDateTime.toLocalTime()
        }
        rvReminders.adapter = reminderAdapter
        /**
         * ushbu fragmentga qaytganda, so'nggi inputlarni yana set qilib qo'yish
         */
        with(datingDataHolder) {
            startTime?.let { setStartTime(it) }
            startDate?.let { setStartDate(it) }
            endTime?.let { setEndTime(it) }
            endDate?.let { setEndDate(it) }
            reminderDate?.let { setReminderDate(it) }
            files?.let { tvFilesCount.text = it.size.toString() }
            importance?.let { setImportance(it) }
            repeatString?.let { setRepeat(it) }
            reminderDate?.let { setReminderDate(it) }
//            partnerIn?.let { setPartnerIn(it) }
            reminders?.let { reminderAdapter.submitList(it.toMutableList()) }
            if (importance == null) {
                setImportance(Pair("green", "Past"))
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        createDatingBlueButton.setOnClickListener {
            hideKeyboard()
            createDating()
        }
        btnCreateDating.setOnClickListener {
            hideKeyboard()
            createDating()
        }
        imgBackButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        reminderLayout.setDropDownClick(
            requireContext(),
            rvReminders,
            null
        )
        cvImportance.setOnClickListener {
            showImportanceDialog(this@CreateDatingFragment::setImportance)
        }
//        removePartner.setOnClickListener {
//            partnerInLayout.isVisible = false
//            etPartnerOut.isVisible = true
//            datingDataHolder.partnerIn = null
//        }

        ivSelectPartner.setOnClickListener {
            storage.participant = OBSERVERS
            if (datingDataHolder.partnerIn == null) {
                storage.persons = emptySet()
            } else {
                storage.persons = datingDataHolder.partnerIn!!.map { it.id.toString() }.toSet()
            }
            showSelectPersonFragment(getString(R.string.title_select_partner))
        }

        cvFiles.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(
                CreateDatingFragmentDirections.toSelectedFilesFragment(
                    datingDataHolder.files?.toFilesItemList()?.toTypedArray()
                        ?: emptyArray(), true
                )
            )
        }

        getDateTime()
        startDateTimeLayout.setOnClickListener {
            showDateTimePickerDialog(
                this@CreateDatingFragment::setStartTime,
                this@CreateDatingFragment::setStartDate
            )
        }

        endDateTimeLayout.setOnClickListener {
            showDateTimePickerDialog(
                this@CreateDatingFragment::setEndTime,
                this@CreateDatingFragment::setEndDate
            )
        }

        tvStartTime.setOnClickListener {
            showTimePickerDialog(this@CreateDatingFragment::setStartTime)
        }

        tvEndTime.setOnClickListener {
            showTimePickerDialog(this@CreateDatingFragment::setEndTime)
        }

        tvStartDate.setOnClickListener {
            showDatePickerDialog(onDateSelected = this@CreateDatingFragment::setStartDate)
        }

        tvEndDate.setOnClickListener {
            showDatePickerDialog(onDateSelected = this@CreateDatingFragment::setEndDate)
        }

        if (datingDataHolder.reminders == null) {
            datingDataHolder.reminders = mutableSetOf()
            addReminder(Pair("O'z vaqtida", 0))
        }
        ivAddReminder.setOnClickListener {
            ReminderNoteDialog(
                requireContext(), this@CreateDatingFragment::addReminder,
            ).show()
        }

        if (datingDataHolder.repeatString == null) {
            setRepeat("Bir marta")
        }
        repeatLayout.setOnClickListener {
            showRepeatNoteDialog(this@CreateDatingFragment::setRepeat)
        }
        repeatTypeLayout.setOnClickListener {
            val repeat = binding.repeatText.text.toString()
            showRepeatRule(repeat)
        }

        reminderDateLayout.setOnClickListener {
            showReminderDateDialog(
                if (reminderDateText.text.isNullOrBlank()) null else
                    LocalDateTime.parse(reminderDateText.text.toString(), dateTimeFormatter),
                this@CreateDatingFragment::setReminderDate
            )
        }
    }

    private fun setupObservers() = with(viewModel) {
        createResponse.simpleCollect(
            this@CreateDatingFragment,
            binding.progressLayout.progressLoader
        ) {
            if (startDate != null) {
                credential.selectedAccountName = storage.googleCalendarAccountName
                syncGoogleCalendar()
            }
            binding.progressLayout.progressLoader.isVisible = false
            sharedViewModel.setDatingNeedsRefresh(true)
            makeSuccessSnack("Uchrashuv qo'shildi")
            navController.navigateUp()
        }

        /**
         * MASTER va NEW_FILES ni observer qilib turish
         * SelectPersonFragment va SelectedFilesFragment dan keladi
         */
        navController.currentBackStackEntry?.savedStateHandle?.let {
            it.getLiveData<List<PersonData>>(OBSERVERS)
                .observe(viewLifecycleOwner) {
                    datingDataHolder.partnerIn = it
                    binding.partnerInLayout.removeAllViews()
                    for (i in it.indices) {
                        if (i < 3)
                            setPartnerIn(it[i], i)
                        else {
                            setPartnerCount(it.size-3)
                            break
                        }
                    }
                }

            it.getLiveData<List<File>?>(NEW_FILES).observe(viewLifecycleOwner) {
                if (datingDataHolder.files == null) {
                    datingDataHolder.files = mutableListOf()
                }
                datingDataHolder.files?.clear()
                datingDataHolder.files?.addAll(it)
                binding.tvFilesCount.text = "${datingDataHolder.files?.size ?: 0}"
            }
        }
    }

    private fun createDating() = with(binding) {
        val isInputCompleted = isInputCompleted(
            listOf(
                Triple(
                    etAddress.text.isNullOrBlank(),
                    etAddress,
                    getString(R.string.error_address)
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
                    datingDataHolder.partnerIn == null && etPartnerOut.text.isNullOrBlank(),
                    etPartnerOut,
                    getString(R.string.error_partner)
                ),
                Triple(
                    binding.repeatText.text.isBlank(),
                    binding.repeatText,
                    getString(R.string.error_repeat)
                ),
                Triple(
                    binding.repeatTypeText.text.isBlank() && datingDataHolder.repeatString != listRepetitionServer[0] && datingDataHolder.repeatString != listRepetitionServer[1] && datingDataHolder.repeatString != null,
                    binding.repeatTypeTitle,
                    getString(R.string.error_repeat_type)
                ),
            ), nsvRoot
        )

        if (isInputCompleted) {
            viewModel.createDating(CreateDatingRequest(
                etDatingDescription.text.toString(),
                etAddress.text.toString(),
                datingDataHolder.startDate?.toUiDate() + " " + datingDataHolder.startTime?.toUiTime(),
                datingDataHolder.endDate?.toUiDate() + " " + datingDataHolder.endTime?.toUiTime(),
                datingDataHolder.files,
                datingDataHolder.importance!!.first,
                datingDataHolder.reminders?.map { it.minutesTime } ?: emptyList(),
                if (datingDataHolder.repeatString == "once") null else datingDataHolder.reminderDate?.toUiDateTime(),
                datingDataHolder.repeatString ?: "once",
                datingDataHolder.partnerIn?.map { it.id },
                etPartnerOut.text?.toString(),
                if (datingDataHolder.repeatString == listRepetitionServer[2]) {
                    datingDataHolder.repeatWeekRule
                } else {
                    datingDataHolder.repeatMonthRule
                },
            ))
        }
    }

    private fun setStartTime(time: LocalTime) {
        time.isStartTimeValid(datingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvStartTime.text = time.toUiTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setEndTime(time: LocalTime) {
        time.isEndTimeValid(datingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvEndTime.text = time.toUiTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setStartDate(date: LocalDate) {
        date.isStartDateValid(datingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvStartDate.text = date.toUiDate()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setEndDate(date: LocalDate) {
        date.isEndDateValid(datingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.tvEndDate.text = date.toUiDate()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun setReminderDate(dateTime: LocalDateTime) {
        dateTime.isReminderDateValid(datingDataHolder).let { resultPair ->
            if (resultPair.first) {
                binding.reminderDateText.text = dateTime.toUiDateTime()
            } else {
                makeErrorSnack(resultPair.second)
            }
        }
    }

    private fun addReminder(pair: Pair<String, Int>) {
        val reminder = NoteReminder(pair.first, pair.second)
        datingDataHolder.reminders?.add(reminder)
        reminderAdapter.submitList(datingDataHolder.reminders?.toMutableList())
    }

    private fun onDeleteReminder(reminder: NoteReminder) {
        if (datingDataHolder.reminders == null) {
            datingDataHolder.reminders = mutableSetOf()
        }
        datingDataHolder.reminders?.remove(reminder)
        reminderAdapter.submitList(datingDataHolder.reminders?.toMutableList())
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
        binding.repeatText.text = repeat
        datingDataHolder.repeatString = changeRepeatMode(repeat)

        when (repeat) {
            getString(R.string.every_week) -> {
                datingDataHolder.repeatWeekRule =
                    2.0.pow((datingDataHolder.startDate ?: LocalDate()).dayOfWeek.toDouble() - 1)
                        .toInt()
                repeatWeekText(datingDataHolder.repeatWeekRule)
            }
            getString(R.string.every_month) -> {
                datingDataHolder.repeatMonthRule = 1
                repeatMonthYearText(datingDataHolder.repeatMonthRule)
            }
            getString(R.string.every_year) -> {
                datingDataHolder.repeatMonthRule = 1
                repeatMonthYearText(datingDataHolder.repeatMonthRule)
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
                RepeatRuleWeeklyDialog(requireContext(), datingDataHolder.repeatWeekRule) { it ->
                    datingDataHolder.repeatWeekRule = it
                    repeatWeekText(it)
                }.show()
            }
            getString(R.string.every_month) -> {
                RadioGroupDialog(
                    requireContext(),
                    datingDataHolder.startDate?.toDateTime(LocalTime()) ?: DateTime(),
                    datingDataHolder.repeatMonthRule,
                    true
                ) { it ->
                    datingDataHolder.repeatMonthRule = it
                    repeatMonthYearText(it)
                }.show()
            }
            getString(R.string.every_year) -> {
                RadioGroupDialog(
                    requireContext(),
                    datingDataHolder.startDate?.toDateTime(LocalTime()) ?: DateTime(),
                    datingDataHolder.repeatMonthRule,
                    false
                ) { it ->
                    datingDataHolder.repeatMonthRule = it
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
        datingDataHolder.importance = pair
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
            datingDataHolder.startDate = LocalDate(System.currentTimeMillis())
            datingDataHolder.endDate = LocalDate(System.currentTimeMillis()+3600*1000)
            datingDataHolder.startTime = LocalTime(System.currentTimeMillis())
            datingDataHolder.endTime = LocalTime(System.currentTimeMillis()+3600*1000)

            tvStartDate.text = datingDataHolder.startDate.toString()
            tvEndDate.text = datingDataHolder.endDate.toString()
            tvStartTime.text = datingDataHolder.startTime.toString().substring(0, 5)
            tvEndTime.text = datingDataHolder.endTime.toString().substring(0, 5)
        }
    }

    private fun setPartnerIn(person: PersonData?, index: Int) = with(binding) {
        if (partnerInLayout.childCount < 3) {
            val partner = LayoutDatingPartnerBinding.inflate(LayoutInflater.from(requireContext()))
            person?.image?.let {
                partner.partnerImageAvatar.loadImageUrl(it)
            }
            partner.root.layoutParams = FrameLayout.LayoutParams(100, 100).apply {
                marginStart = (index + 1) * (90)
            }
            partnerInLayout.addView(partner.root)
        }
//        if (person != null) {
//            etPartnerOut.setText("")
//        }
//        partnerInLayout.isVisible = person != null
//        etPartnerOut.isVisible = person == null
//        datingDataHolder.partnerIn = person
//        partnerInName.text = person?.fullName
    }

    private fun syncGoogleCalendar() {
        launch {
            val event: Event = Event()
                .setSummary(binding.etDatingDescription.text.toString())
                .setDescription("Majlis Vaqti")

            val startDateTime = com.google.api.client.util.DateTime("${datingDataHolder.startDate?.toUiDate()}T${datingDataHolder.startTime?.toUiTime()}:00-07:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Tashkent")
            event.setStart(start)

            val endDateTime = com.google.api.client.util.DateTime("${datingDataHolder.endDate?.toUiDate()}T${datingDataHolder.endTime?.toUiTime()}:00-07:00")
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

    private fun setPartnerCount(count: Int) {
        val text = TextView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(100, 100).apply {
                marginStart = (4) * (90)
            }
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.round_person_blue_bg)
            setTextColor(getColor(requireContext(), R.color.white))
            text = "+$count"
            gravity = Gravity.CENTER
        }
        binding.partnerInLayout.addView(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = job
}