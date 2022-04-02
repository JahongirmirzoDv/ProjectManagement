package uz.perfectalgorithm.projects.tezkor.utils.adding

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.QuickPlanByTimeEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.QuickPlanByTypeEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardGoal
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.repitition.RepetitionData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.databinding.*
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.RepeatAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.adding.StatusAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.dashboard.DashboardGoalAdapter
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.setTintColor
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.util.*

/**
 *Created by farrukh_kh on 8/10/21 4:43 PM
 *uz.rdo.projects.projectmanagement.utils.adding
 **/
fun Fragment.showImportanceDialog(
    onImportanceSelected: SingleBlock<Pair<String, String>>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemImportanceDialogBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        highStatus.setOnClickListener {
            dialog.dismiss()
            onImportanceSelected(Pair("red", "Yuqori"))
        }
        averageStatus.setOnClickListener {
            dialog.dismiss()
            onImportanceSelected(Pair("yellow", "O'rta"))
        }
        good.setOnClickListener {
            dialog.dismiss()
            onImportanceSelected(Pair("green", "Past"))
        }
    }
    dialog.show()
}

fun Fragment.showStatusDialog(
    statusList: List<StatusData>,
    onStatusSelected: SingleBlock<StatusData>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemStatusChangeDialogBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        rvStatus.adapter = StatusAdapter(statusList) {
            dialog.dismiss()
            onStatusSelected(it)
        }
    }
    dialog.show()
}

fun Fragment.showStaticStatusDialog(onStatusSelected: SingleBlock<Pair<String, String>>) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemStatusDialogBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        newStatus.setOnClickListener {
            onStatusSelected(Pair("new", "Yangi"))
            dialog.dismiss()
        }
        beingDone.setOnClickListener {
            onStatusSelected(Pair("in_progress", "Bajarilmoqda"))
            dialog.dismiss()
        }
        done.setOnClickListener {
            onStatusSelected(Pair("done", "Bajarilgan"))
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showEditRuleDialog(canEdit: Boolean = false, onSelected: SingleBlock<Boolean>) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = DialogSelectEditTimeRuleBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    binding.apply {
        if (canEdit) {
            tvCan.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            ivCan.setTintColor(R.color.blue)
        } else {
            tvCanNot.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            ivCanNot.setTintColor(R.color.blue)
        }
        viewCan.setOnClickListener {
            onSelected(true)
            dialog.dismiss()
        }
        viewCanNot.setOnClickListener {
            onSelected(false)
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showQuickPlanTimeDialog(
    byTime: QuickPlanByTimeEnum = QuickPlanByTimeEnum.BY_WEEK,
    onSelected: SingleBlock<QuickPlanByTimeEnum>
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = DialogBottomQuickPlanTimeBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    binding.apply {
        when (byTime) {
            QuickPlanByTimeEnum.BY_DAY -> {
                dayTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                dayIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTimeEnum.BY_WEEK -> {
                weekTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                weekIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTimeEnum.BY_MONTH -> {
                monthTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                monthIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTimeEnum.BY_YEAR -> {
                yearTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                yearIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTimeEnum.ALL -> {
                allTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                allIcon.setTintColor(R.color.blue)
            }
        }
        dayLayout.setOnClickListener {
            onSelected(QuickPlanByTimeEnum.BY_DAY)
            dialog.dismiss()
        }
        weekLayout.setOnClickListener {
            onSelected(QuickPlanByTimeEnum.BY_WEEK)
            dialog.dismiss()
        }
        monthLayout.setOnClickListener {
            onSelected(QuickPlanByTimeEnum.BY_MONTH)
            dialog.dismiss()
        }
        yearLayout.setOnClickListener {
            onSelected(QuickPlanByTimeEnum.BY_YEAR)
            dialog.dismiss()
        }
        allLayout.setOnClickListener {
            onSelected(QuickPlanByTimeEnum.ALL)
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showQuickPlanTypeDialog(
    type: QuickPlanByTypeEnum = QuickPlanByTypeEnum.ALL,
    onSelected: SingleBlock<QuickPlanByTypeEnum>
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = DialogBottomQuickPlanTypeBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    binding.apply {
        when (type) {
            QuickPlanByTypeEnum.ALL -> {
                allTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                allIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTypeEnum.DONE -> {
                doneTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                doneIcon.setTintColor(R.color.blue)
            }
            QuickPlanByTypeEnum.UNDONE -> {
                undoneTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                undoneIcon.setTintColor(R.color.blue)
            }
        }
        allLayout.setOnClickListener {
            onSelected(QuickPlanByTypeEnum.ALL)
            dialog.dismiss()
        }
        doneLayout.setOnClickListener {
            onSelected(QuickPlanByTypeEnum.DONE)
            dialog.dismiss()
        }
        undoneLayout.setOnClickListener {
            onSelected(QuickPlanByTypeEnum.UNDONE)
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showDashboardGoalDialog(
    selectedGoal: DashboardGoal?,
    goals: List<DashboardGoal>,
    onSelected: SingleBlock<DashboardGoal>
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = DialogBottomDashboardGoalBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    binding.apply {
        rvGoals.adapter = DashboardGoalAdapter {
            onSelected(it)
            dialog.dismiss()
        }.apply {
            submitList(goals.map { it.copy(isSelected = (it.id == selectedGoal?.id)) })
        }
    }
    dialog.show()
}

fun Fragment.showRepeatDialog(
    repeats: List<RepetitionData>,
    onRepeatSelected: SingleBlock<RepetitionData>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemRepeatDialogBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        rvRepeats.adapter = RepeatAdapter(repeats) {
            dialog.dismiss()
            onRepeatSelected(it)
        }
    }
    dialog.show()
}


fun Fragment.showStatusTaskProjectDialog(
    currentInternalStatus: String?,
    onStatusSelected: SingleBlock<Triple<String, String, Int>>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemStatusTaskVsProjectBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.apply {
        when (currentInternalStatus) {
            getString(R.string.new__) -> {
                beginLayout.visible()
                receiveLayout.visible()
                doneLayout.visible()
                rejectLayout.visible()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.hide()
            }
            getString(R.string.begin_) -> {
                beginLayout.hide()
                receiveLayout.visible()
                doneLayout.visible()
                rejectLayout.visible()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.hide()
            }
            getString(R.string.receive_) -> {
                beginLayout.visible()
                receiveLayout.hide()
                doneLayout.visible()
                rejectLayout.visible()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.hide()
            }
            getString(R.string.done) -> {
                beginLayout.visible()
                receiveLayout.hide()
                doneLayout.hide()
                rejectLayout.hide()
                ownerLayout.visible()
                leaderLayout.visible()
                rejectReceiveLayout.hide()
            }
            getString(R.string.reject_) -> {
                beginLayout.visible()
                receiveLayout.hide()
                doneLayout.hide()
                rejectLayout.hide()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.visible()
            }
            getString(R.string.reject_receive_) -> {
                beginLayout.visible()
                receiveLayout.hide()
                doneLayout.hide()
                rejectLayout.hide()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.hide()
            }
            getString(R.string.leader_check_), getString(R.string.owner_check_) -> {
                beginLayout.visible()
                receiveLayout.hide()
                doneLayout.hide()
                rejectLayout.visible()
                ownerLayout.hide()
                leaderLayout.hide()
                rejectReceiveLayout.hide()
            }
        }


        beginLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.begin_),
                    getString(R.string.begin),
                    R.drawable.ic_begin
                )
            )
        }
        receiveLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.receive_),
                    getString(R.string.receive),
                    R.drawable.ic_check
                )
            )
        }
        doneLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.done_),
                    getString(R.string.done),
                    R.drawable.ic_done_square
                )
            )
        }
        rejectLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.reject_),
                    getString(R.string.reject),
                    R.drawable.ic_reject
                )
            )
        }
        ownerLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.owner_check_),
                    getString(R.string.owner_check),
                    R.drawable.ic_owner_check
                )
            )
        }
        leaderLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.leader_check_),
                    getString(R.string.leader_check),
                    R.drawable.ic_leader_check
                )
            )
        }
        rejectReceiveLayout.setOnClickListener {
            dialog.dismiss()
            onStatusSelected(
                Triple(
                    getString(R.string.reject_receive_),
                    getString(R.string.reject_receive),
                    R.drawable.ic_reject_recevie
                )
            )
        }
    }
    dialog.show()
}


fun Fragment.showReminderDateDialog(
    defaultDateTime: LocalDateTime? = null,
    onReminderDateSelected: SingleBlock<LocalDateTime>,
) {
    hideKeyboard()
    val calendar = Calendar.getInstance()
    val calendarYear = defaultDateTime?.year ?: calendar.get(Calendar.YEAR)
    val calendarMonth = defaultDateTime?.monthOfYear?.minus(1) ?: calendar.get(Calendar.MONTH)
    val calendarDay = defaultDateTime?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)
    val calendarHour = defaultDateTime?.hourOfDay ?: calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = defaultDateTime?.minuteOfHour ?: calendar.get(Calendar.MINUTE)

    var selectedDateTime = LocalDateTime()

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            selectedDateTime = selectedDateTime.withHourOfDay(hour).withMinuteOfHour(minute)
            onReminderDateSelected(selectedDateTime)
        },
        calendarHour,
        calendarMinute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, month, day ->
            selectedDateTime = selectedDateTime.withYear(year)
                .withMonthOfYear(month + 1)
                .withDayOfMonth(day)
            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}

fun Fragment.showDateTimePickerDialog(
    onTimeSelected: SingleBlock<LocalTime>,
    onDateSelected: SingleBlock<LocalDate>,
) {
    hideKeyboard()
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            onTimeSelected(LocalTime(hour, minute))
        },
        calendarHour,
        calendarMinute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        R.style.DialogTheme,
        { _, year, month, day ->
            onDateSelected(LocalDate(year, month + 1, day))
            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}

fun Fragment.showDateTimePickerDialog(
    onDateTimeSelected: SingleBlock<LocalDateTime>
) {
    hideKeyboard()
    var dateTime = LocalDateTime()
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            dateTime = dateTime.withTime(hour, minute, 0, 0)
            onDateTimeSelected(dateTime)
        },
        calendarHour,
        calendarMinute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        R.style.DialogTheme,
        { _, year, month, day ->
            dateTime = dateTime.withDate(year, month + 1, day)
            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}

fun Fragment.showTimePickerDialog(
    onTimeSelected: SingleBlock<LocalTime>,
) {
    hideKeyboard()
    val calendar = Calendar.getInstance()
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            onTimeSelected(LocalTime(hour, minute))
        },
        calendarHour,
        calendarMinute,
        true
    )
    timePickerDialog.show()
}

fun Fragment.showDatePickerDialog(
    defaultDate: LocalDate? = null,
    onDateSelected: SingleBlock<LocalDate>,
) {
    hideKeyboard()
    val calendar = Calendar.getInstance()
    val calendarYear = defaultDate?.year ?: calendar.get(Calendar.YEAR)
    val calendarMonth = defaultDate?.monthOfYear?.minus(1) ?: calendar.get(Calendar.MONTH)
    val calendarDay = defaultDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, month, day ->
            onDateSelected(LocalDate(year, month + 1, day))
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}

fun Fragment.showWeekPickerDialog(
    defaultDate: LocalDate? = null,
    onDateSelected: SingleBlock<LocalDate>,
) {
    hideKeyboard()
    val calendar = Calendar.getInstance()
    val calendarYear = defaultDate?.year ?: calendar.get(Calendar.YEAR)
    val calendarMonth = defaultDate?.monthOfYear?.minus(1) ?: calendar.get(Calendar.MONTH)
    val calendarDay = defaultDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, month, day ->
            onDateSelected(LocalDate(year, month + 1, day).withDayOfWeek(1))
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}

fun Fragment.showMonthPickerDialog(
    defaultDate: LocalDate?,
    onMonthSelected: SingleBlock<LocalDate>
) {
    hideKeyboard()
    val dialog = AppCompatDialog(requireContext())
    val binding = DialogPickMonthBinding.inflate(LayoutInflater.from(context))
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.setContentView(binding.root)
    binding.apply {
        npMonth.apply {
            minValue = 1
            maxValue = 12
            displayedValues = getMonths()
            if (defaultDate != null) {
                value = defaultDate.monthOfYear
            }
        }
        npYear.apply {
            getYears().let { years ->
                minValue = years.first()
                maxValue = years.last()
                if (defaultDate != null) {
                    value = defaultDate.year
                }
            }
        }
        btnSubmit.setOnClickListener {
            onMonthSelected(LocalDate(npYear.value, npMonth.value, 1))
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showYearPickerDialog(
    defaultDate: LocalDate?,
    onYearSelected: SingleBlock<LocalDate>
) {
    hideKeyboard()
    val dialog = AppCompatDialog(requireContext())
    val binding = DialogPickYearBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    binding.apply {
        npYear.apply {
            getYears().let { years ->
                minValue = years.first()
                maxValue = years.last()
                if (defaultDate != null) {
                    value = defaultDate.year
                }
            }
        }
        btnSubmit.setOnClickListener {
            onYearSelected(LocalDate(npYear.value, 1, 1))
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun Fragment.showReminderDialog(
    onReminderSelected: SingleBlock<Pair<Int, String>>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemReminderDialogBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        minute0.setOnClickListener {
            dialog.dismiss()
            onReminderSelected(Pair(0, "O'z vaqtida"))
        }
        minute5.setOnClickListener {
            dialog.dismiss()
            onReminderSelected(Pair(5, "5 daqiqa oldin"))
        }
        minute15.setOnClickListener {
            dialog.dismiss()
            onReminderSelected(Pair(15, "15 daqiqa oldin"))
        }
        minute30.setOnClickListener {
            dialog.dismiss()
            onReminderSelected(Pair(30, "30 daqiqa oldin"))
        }
    }
    dialog.show()
}

fun Fragment.showConvertToDialog(
    onToProjectClick: EmptyBlock,
    onToTaskClick: EmptyBlock,
    isEditorMode: Boolean = false,
    onDeleteClick: EmptyBlock? = null
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = DialogConvertToBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        delete.isVisible = isEditorMode
        toProject.setOnClickListener {
            dialog.dismiss()
            onToProjectClick()
        }
        toTask.setOnClickListener {
            dialog.dismiss()
            onToTaskClick()
        }
        delete.setOnClickListener {
            dialog.dismiss()
            onDeleteClick?.invoke()
        }
    }
    dialog.show()
}

fun getYears(): Array<Int> {
    val year = LocalDate().year
    return arrayOf(year, year + 1, year + 2, year + 3)
}

fun getMonths() = arrayOf(
    "Yanvar",
    "Fevral",
    "Mart",
    "Aprel",
    "May",
    "Iyun",
    "Iyul",
    "Avgust",
    "Sentyabr",
    "Oktyabr",
    "Noyabr",
    "Dekabr",
)