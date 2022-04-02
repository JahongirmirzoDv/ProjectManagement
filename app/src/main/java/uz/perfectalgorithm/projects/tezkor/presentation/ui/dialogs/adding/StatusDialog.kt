package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.ItemStatusDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard

/**
 * Created by Jasurbek Kurganbaev on 01.07.2021 11:40
 **/
object StatusDialog {
    fun showStatusDialog(
        context: Context,
        newStatusResponse: EmptyBlock,
        inProgressStatusResponse: EmptyBlock,
        finishedStatusResponse: EmptyBlock,
        fragment: Fragment
    ) {
        fragment.hideKeyboard()
        val dialog = BottomSheetDialog(context)
        val binding = ItemStatusDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            newStatus.setOnClickListener { dialog.dismiss(); newStatusResponse() }
            beingDone.setOnClickListener { dialog.dismiss(); inProgressStatusResponse() }
            done.setOnClickListener { dialog.dismiss(); finishedStatusResponse() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }

}