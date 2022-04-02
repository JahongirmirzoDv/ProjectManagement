package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.create_goal_map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.AddGoalMapDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock


object CreateMapDialog {
    fun showSectionsBottomSheetDialog(
        context: Context,
        title: String,
        editingData: String? = null,
        response: SingleBlock<String>
    ) {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogThemeNoFloating)
        val binding = AddGoalMapDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            binding.textView15.text = title
            if (editingData != null) {
                binding.etGoalMapName.setText(editingData)
            }
            buttonCancel.setOnClickListener { dialog.dismiss() }
            btnCreate.setOnClickListener {
                if (binding.etGoalMapName.text!!.isEmpty()) {
                    if (title == "Maqsad xarita qo'shish") {
                        binding.etGoalMapName.error = context.getString(R.string.map_name_error)
                    } else {
                        binding.etGoalMapName.error =
                            context.getString(R.string.folder_name_error)
                    }
                } else {
                    response(binding.etGoalMapName.text.toString())
                    dialog.dismiss()
                }
            }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }


}
