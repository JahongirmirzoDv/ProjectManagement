package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.SeekBar
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.ItemNoteReminderDialogBinding

class ReminderNoteDialog(
    private val fragmentContext: Context,
    val callback: (selectItem: Pair<String, Int>) -> Unit,
) : AlertDialog(fragmentContext) {
    private var _binding: ItemNoteReminderDialogBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    init {
        _binding = ItemNoteReminderDialogBinding.inflate(layoutInflater)
        setView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        binding.okBtn.setOnClickListener {
            getReminderDate(binding.seekbarDate.progress).let { it1 -> callback.invoke(it1) }
            dismiss()
        }
        binding.seekbarDate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvReminderDate.text = getReminderDate(progress).first
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun getReminderDate(progress: Int): Pair<String, Int> = when (progress) {
        0 -> {
            Pair(fragmentContext.getString(R.string.ab1), 0)
        }
        1 -> {
            Pair(fragmentContext.getString(R.string.vaqt1), 5)
        }
        2 -> {
            Pair(fragmentContext.getString(R.string.vaqt2), 10)
        }
        3 -> {
            Pair(fragmentContext.getString(R.string.vaqt3), 15)
        }
        4 -> {
            Pair(fragmentContext.getString(R.string.vaqt4), 30)
        }
        5 -> {
            Pair(fragmentContext.getString(R.string.vaqt5), 60)
        }
        6 -> {
            Pair(fragmentContext.getString(R.string.vaqt6), 2 * 60)
        }
        7 -> {
            Pair(fragmentContext.getString(R.string.vaqt7), 3 * 60)
        }
        8 -> {
            Pair(fragmentContext.getString(R.string.vaqt8), 5 * 60)
        }
        9 -> {
            Pair(fragmentContext.getString(R.string.vaqt9), 10 * 60)
        }
        10 -> {
            Pair(fragmentContext.getString(R.string.vaqt10), 24 * 60)
        }
        11 -> {
            Pair(fragmentContext.getString(R.string.vaqt11), 2 * 24 * 60)
        }
        else -> {
            Pair(fragmentContext.getString(R.string.vaqt12), 7 * 24 * 60)
        }
    }
}