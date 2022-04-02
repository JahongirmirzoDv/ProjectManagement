package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.personal


/**
 * Craeted by Davronbek Raximjanov on 12.08.2021
 * **/

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.DialogDeleteChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

class DeleteChatDialog(
    private val activity: Activity,
) : AlertDialog(activity) {

    private var _binding: DialogDeleteChatBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var listenDelete: EmptyBlock? = null

    init {
        _binding = DialogDeleteChatBinding.inflate(layoutInflater)
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




