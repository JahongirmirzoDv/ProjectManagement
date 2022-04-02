package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.ItemRepeatNoteBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard


fun Fragment.showRepeatNoteDialog(
    onRepeatSelected: SingleBlock<String>,
) {
    hideKeyboard()
    val dialog = BottomSheetDialog(requireContext())
    val binding = ItemRepeatNoteBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(binding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.apply {
        tvOnce.setOnClickListener {
            onRepeatSelected.invoke((it as TextView).text.toString())
            dialog.dismiss()
        }
        everyDay.setOnClickListener {
            onRepeatSelected.invoke((it as TextView).text.toString())
            dialog.dismiss()
        }
        everyWeek.setOnClickListener {
            onRepeatSelected.invoke((it as TextView).text.toString())
            dialog.dismiss()
        }
        everyMonth.setOnClickListener {
            onRepeatSelected.invoke((it as TextView).text.toString())
            dialog.dismiss()
        }
        everyYear.setOnClickListener {
            onRepeatSelected.invoke((it as TextView).text.toString())
            dialog.dismiss()
        }
    }
    dialog.show()
}



