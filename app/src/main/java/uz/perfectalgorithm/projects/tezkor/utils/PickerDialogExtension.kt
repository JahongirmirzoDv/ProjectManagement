package uz.perfectalgorithm.projects.tezkor.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_goal.CreateGoalViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_project.CreateProjectViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.adding.create_task.CreateTaskViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jasurbek Kurganbaev on 01.07.2021 14:52
 **/

fun timePickerDialogGoal(
    timeView: TextView,
    context: Context,
    viewModel: CreateGoalViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)
    fragment.hideKeyboard()

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime
            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime
            }
        },
        calendarHour,
        calendarMinute,
        true
    )
    timePickerDialog.show()
}


fun datePickerDialogGoal(
    dateView: TextView,
    context: Context,
    viewModel: CreateGoalViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    fragment.hideKeyboard()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()

}


fun dateAndTimePickerDialogGoal(
    dateView: TextView,
    timeView: TextView,
    context: Context,
    viewModel: CreateGoalViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)
    fragment.hideKeyboard()

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime

            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime

            }
        },
        calendarHour,
        calendarMinute,
        true
    )


    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }

            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}


fun timePickerDialogProject(
    timeView: TextView,
    context: Context,
    viewModel: CreateProjectViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)
    fragment.hideKeyboard()

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime
            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime
            }
        },
        calendarHour,
        calendarMinute,
        true
    )
    timePickerDialog.show()
}


fun datePickerDialogProject(
    dateView: TextView,
    context: Context,
    viewModel: CreateProjectViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    fragment.hideKeyboard()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()

}

fun dateAndTimePickerDialogProject(
    dateView: TextView,
    timeView: TextView,
    context: Context,
    viewModel: CreateProjectViewModel,
    fragment: Fragment,
) {
    fragment.hideKeyboard()
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
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime || timeView.id == R.id.tv_start_time) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime
            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime
            }

        },
        calendarHour,
        calendarMinute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DialogTheme,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime || dateView.id == R.id.tv_start_date) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }

            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()
}


fun timePickerDialogTask(
    timeView: TextView,
    context: Context,
    viewModel: CreateTaskViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)
    fragment.hideKeyboard()

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime || timeView.id == R.id.tv_start_time) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime
            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime
            }
        },
        calendarHour,
        calendarMinute,
        true
    )
    timePickerDialog.show()
}


fun datePickerDialogTask(
    dateView: TextView,
    context: Context,
    viewModel: CreateTaskViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    fragment.hideKeyboard()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime || dateView.id == R.id.tv_start_date) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )

    datePickerDialog.show()

}


fun dateAndTimePickerDialogTask(
    dateView: TextView,
    timeView: TextView,
    context: Context,
    viewModel: CreateTaskViewModel,
    fragment: Fragment,
) {
    val calendar = Calendar.getInstance()
    val calendarYear = calendar.get(Calendar.YEAR)
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinute = calendar.get(Calendar.MINUTE)
    fragment.hideKeyboard()
    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DialogTheme,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            if (timeView.id == R.id.tvBeginTime || timeView.id == R.id.tv_start_time) {
                viewModel.startTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.startTime
            } else {
                viewModel.endTime = SimpleDateFormat("HH:mm").format(calendar.time)
                timeView.text = viewModel.endTime
            }

        },
        calendarHour,
        calendarMinute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DialogTheme,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, year)
            if (dateView.id == R.id.tvBeginningTime || dateView.id == R.id.tv_start_date) {
                viewModel.startDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.startDate
            } else {
                viewModel.endDate = String.format(
                    "%02d-%02d-%02d", year, month + 1, day
                )
                dateView.text = viewModel.endDate
            }

            timePickerDialog.show()
        },
        calendarYear,
        calendarMonth,
        calendarDay
    )
    datePickerDialog.show()
}