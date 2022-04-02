package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogDeleteBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class DeleteDialog(
    private val contextFragment: Context
) : AlertDialog(contextFragment) {

    private var _binding: FragmentDialogDeleteBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    private var yesListener: SingleBlock<Boolean>? = null

    init {
        _binding = FragmentDialogDeleteBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.apply {

            btnDelete.setOnClickListener {
                yesListener?.invoke(true)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun deleteClickListener(f: SingleBlock<Boolean>) {
        yesListener = f
    }

}
