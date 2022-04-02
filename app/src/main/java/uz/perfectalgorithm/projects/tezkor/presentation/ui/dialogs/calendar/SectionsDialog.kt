package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.databinding.CalendarSectionsBottomsheetBinding
import uz.perfectalgorithm.projects.tezkor.databinding.DialogBottomCalendarBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock


object CalendarSectionsDialog {
    fun showSectionsBottomSheetDialog(
        context: Context,
        openCreateNoteScreen: EmptyBlock,
        openCreateTasksScreen: EmptyBlock,
        openCreateMeetingScreen: EmptyBlock,
        openCreateUchrashuvScreen: EmptyBlock,
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = CalendarSectionsBottomsheetBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            note.setOnClickListener { dialog.dismiss();openCreateNoteScreen() }
            functions.setOnClickListener { dialog.dismiss();openCreateTasksScreen() }
            assembly.setOnClickListener { dialog.dismiss();openCreateMeetingScreen() }
            meeting.setOnClickListener { dialog.dismiss();openCreateUchrashuvScreen() }
        }
        dialog.show()
    }

    fun showCalendarBottomSheetDialog(
        context: Context,
        openYearScreen: EmptyBlock,
        openMonthScreen: EmptyBlock,
        openWeekScreen: EmptyBlock,
        openDayScreen: EmptyBlock
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = DialogBottomCalendarBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            yearLayout.setOnClickListener { dialog.dismiss();openYearScreen() }
            monthLayout.setOnClickListener { dialog.dismiss();openMonthScreen() }
            weekLayout.setOnClickListener { dialog.dismiss();openWeekScreen() }
            dayLayout.setOnClickListener { dialog.dismiss();openDayScreen() }
        }
        dialog.show()
    }
}