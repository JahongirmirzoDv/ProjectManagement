package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.entry.reset_password

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.SuccessDialogBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.showAlertDialog


object SuccessDialogMaker {
    fun showSuccessDialog(
        context: Context,
        login: EmptyBlock
    ) {
        val binding = SuccessDialogBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog
            .Builder(context)
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            btnLogin.setOnClickListener { dialog.dismiss();login() }
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}