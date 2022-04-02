package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.structure


/**
 * Craeted by Davronbek Raximjanov on 23.08.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.DialogDeleteWorkerBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

class DeleteWorkerDialog(
    private val activity: Activity,
) : AlertDialog(activity) {

    private var _binding: DialogDeleteWorkerBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenDelete: EmptyBlock? = null

    init {
        _binding = DialogDeleteWorkerBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnDelete.setOnClickListener {
                listenDelete?.invoke()
                dismiss()
            }
        }
    }

    fun deleteClickListener(f: EmptyBlock) {
        listenDelete = f
    }
}




