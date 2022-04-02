package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others


/**
 * Craeted by Davronbek Raximjanov on 26.06.2021
 * **/


import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.DialogLogoutCompanyBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class LogOutDialog(
    private val activity: Activity
) : AlertDialog(activity) {

    private var _binding: DialogLogoutCompanyBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var saveListener: SingleBlock<Boolean>? = null

    init {
        _binding = DialogLogoutCompanyBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnYes.setOnClickListener {
                saveListener?.invoke(true)
                dismiss()
            }

            btnNo.setOnClickListener {
                saveListener?.invoke(false)
                dismiss()
            }
        }
    }

    fun saveClickListener(f: SingleBlock<Boolean>) {
        saveListener = f
    }

}




