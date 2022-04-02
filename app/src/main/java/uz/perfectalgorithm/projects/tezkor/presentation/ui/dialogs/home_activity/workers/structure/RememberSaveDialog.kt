package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure


/**
 * Craeted by Davronbek Raximjanov on 06.09.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.DialogRememberSaveBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard

class RememberSaveDialog(
    private val activity: Activity,
) : AlertDialog(activity) {


    private var _binding: DialogRememberSaveBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenSave: EmptyBlock? = null
    private var listenCancel: EmptyBlock? = null

    init {
        _binding = DialogRememberSaveBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            btnClose.setOnClickListener {
                activity.hideKeyboard()
                listenCancel?.invoke()
                dismiss()
            }

            btnCancel.setOnClickListener {
                activity.hideKeyboard()
                listenCancel?.invoke()
                dismiss()
            }

            btnSave.setOnClickListener {
                activity.hideKeyboard()
                listenSave?.invoke()
                dismiss()
            }
        }
    }

    fun saveClickListener(f: EmptyBlock) {
        listenSave = f
    }

    fun cancelClickListener(f: EmptyBlock) {
        listenCancel = f
    }
}




