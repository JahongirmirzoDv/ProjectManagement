package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.ItemImportanceDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard

/**
 * Created by Jasurbek Kurganbaev on 09.07.2021 17:40
 **/
object ImportanceDialog {
/*    fun showStatusDialog(
        context: Context,
        highImportanceResponse: EmptyBlock,
        middleImportanceResponse: EmptyBlock,
        lowImportanceResponse: EmptyBlock,
        fragment: Fragment
    ) {
        fragment.hideKeyboard()
        val dialog = BottomSheetDialog(context)
        val binding = ItemImportanceDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            highStatus.setOnClickListener { dialog.dismiss(); highImportanceResponse() }
            averageStatus.setOnClickListener { dialog.dismiss(); middleImportanceResponse() }
            good.setOnClickListener { dialog.dismiss(); lowImportanceResponse() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }*/

    fun showStatusDialog(
        context: Context,
        highImportanceResponse: EmptyBlock,
        middleImportanceResponse: EmptyBlock,
        lowImportanceResponse: EmptyBlock,
        fragment: Fragment
    ) {
        fragment.hideKeyboard()
        val dialog = BottomSheetDialog(context)
        val binding = ItemImportanceDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            highStatus.setOnClickListener { dialog.dismiss(); highImportanceResponse() }
            averageStatus.setOnClickListener { dialog.dismiss(); middleImportanceResponse() }
            good.setOnClickListener { dialog.dismiss(); lowImportanceResponse() }
        }
        dialog.show()
    }


}