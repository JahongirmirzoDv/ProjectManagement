package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.QuickSheetDialogLayoutBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock


object QuickSheetDialogMaker {
    fun showBottomSheetDialog(
        context: Context,
        openQuickIdea: EmptyBlock,
        openQuickPlan: EmptyBlock
    ) {
        val dialog = BottomSheetDialog(context)
        val binding = QuickSheetDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            rlQuickIdea.setOnClickListener { dialog.dismiss();openQuickIdea() }
            rlQuickPlan.setOnClickListener { dialog.dismiss(); openQuickPlan() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }
}
