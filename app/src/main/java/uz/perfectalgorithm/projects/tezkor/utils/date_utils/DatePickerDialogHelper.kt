package uz.perfectalgorithm.projects.tezkor.utils.date_utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.DoubleBlock
import java.util.*

/**
 * Created by Davronbek Raximjanov 11.07.2021
 **/

object DatePickerDialogHelper {
    fun showDatePickerDialog(fragmentManager: Context, selectedDate: DoubleBlock<String, String>) {
        val now = Calendar.getInstance()
        val dialog = DatePickerDialog(
            fragmentManager,
            AlertDialog.THEME_HOLO_LIGHT,
            { _, year, monthOfYear, dayOfMonth ->
                val serverSendTime =
                    "$year-${if (monthOfYear >= 9) monthOfYear + 1 else ("0${monthOfYear + 1}")}-${if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth"}"
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val userShowTime =
                    "${if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth"}.${if (monthOfYear >= 9) monthOfYear + 1 else ("0${monthOfYear + 1}")}.$year"
                selectedDate(serverSendTime, userShowTime)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = now.timeInMillis
        dialog.show()
    }
}