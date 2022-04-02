package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.quick_ideas

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.MoreButtonDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

object QuickItemMoreSheetDialogMaker {
    fun showBottomSheetDialog(
        context: Context,
        editIdeasBox: EmptyBlock,
        deleteIdeasBox: EmptyBlock
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = MoreButtonDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            editing.setOnClickListener { dialog.dismiss();editIdeasBox() }
            deleting.setOnClickListener { dialog.dismiss(); deleteIdeasBox() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }
}