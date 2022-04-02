package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.SectionsBottomsheetLayoutBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock


object SectionsDialog {
    fun showSectionsBottomSheetDialog(
        context: Context,
        openCreateNoteScreen: EmptyBlock,
        openCreateTasksScreen: EmptyBlock,
        openCreateProjectScreen: EmptyBlock,
        openCreateMeetingScreen: EmptyBlock,
        openCreateDatingScreen: EmptyBlock
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = SectionsBottomsheetLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            note.setOnClickListener { dialog.dismiss();openCreateNoteScreen() }
            functions.setOnClickListener { dialog.dismiss();openCreateTasksScreen() }
            project.setOnClickListener { dialog.dismiss();openCreateProjectScreen() }
            assembly.setOnClickListener { dialog.dismiss();openCreateMeetingScreen() }
            meeting.setOnClickListener { dialog.dismiss();openCreateDatingScreen() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }
}