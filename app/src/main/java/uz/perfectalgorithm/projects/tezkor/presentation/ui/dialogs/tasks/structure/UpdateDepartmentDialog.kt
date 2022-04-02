package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.tasks.structure

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import uz.perfectalgorithm.projects.tezkor.databinding.DialogEditDepartmentBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock

class UpdateDepartmentDialog(
    contextFragment: Context,
    private val title: String,
    val callback: SingleBlock<String>
) :
    AlertDialog(contextFragment) {

    private var _binding: DialogEditDepartmentBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")


    init {
        _binding = DialogEditDepartmentBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.etName.setText(title)
        binding.btnUpdate.setOnClickListener {
            callback.invoke(binding.etName.text.toString())
            dismiss()
        }
    }
}