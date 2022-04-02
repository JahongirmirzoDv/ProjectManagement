package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogAddModeratorBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

object ModeratorDialog {
    fun showModeratorBottomSheetDialog(
        context: Context,
        isModerator: Boolean,
        eventModerator: EmptyBlock,
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = DialogAddModeratorBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (isModerator) {
            binding.allTitle.text = context.getString(R.string.remove_moderator)
            binding.allTitle.setTextColor(Color.RED)
            binding.allIcon.setColorFilter(Color.RED)
        } else {
            binding.allTitle.text = context.getString(R.string.add_moderator)
            binding.allIcon.setColorFilter(Color.BLACK)
            binding.allTitle.setTextColor(Color.BLACK)
        }
        binding.apply {
            root.setOnClickListener { dialog.dismiss();eventModerator() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }
}