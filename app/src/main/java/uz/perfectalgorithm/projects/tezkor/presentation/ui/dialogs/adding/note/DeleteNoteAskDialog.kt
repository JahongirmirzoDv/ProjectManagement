package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogDeleteNoteBinding

class DeleteNoteAskDialog(
    fragmentContext: Context,
    val callback: (selectItem: Int) -> Unit
) : AlertDialog(fragmentContext) {
    private var _binding: DialogDeleteNoteBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private var selectItem = 0

    init {
        _binding = DialogDeleteNoteBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }


    private fun loadViews() {
        binding.rgDelete.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.one_select -> {
                    selectItem = 0
                }
                R.id.all_note -> {
                    selectItem = 1
                }
            }
        }


        binding.okBtn.setOnClickListener {
            callback.invoke(selectItem)
            dismiss()
        }
        binding.backBtn.setOnClickListener {
            dismiss()
        }
    }


}