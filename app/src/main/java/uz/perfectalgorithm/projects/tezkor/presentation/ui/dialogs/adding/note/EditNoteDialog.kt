package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogEditNoteBinding

class EditNoteDialog(
    private val fragmentContext: Context,
    val callback: (selectItem: Int) -> Unit
) : AlertDialog(fragmentContext) {
    private var _binding: DialogEditNoteBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private var selectItem = 0

    init {
        _binding = DialogEditNoteBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.rgEdit.setOnCheckedChangeListener { group, checkedId ->
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