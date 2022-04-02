package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.SeekBar
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
            Pair("O'z vaqtida", 0)
        }
        1 -> {
            Pair("5 daqiqa oldin", 5)
        }
        2 -> {
            Pair("10 daqiqa oldin", 10)
        }
        3 -> {
            Pair("15 daqiqa oldin", 15)
        }
        4 -> {
            Pair("30 daqiqa oldin", 30)
        }
        5 -> {
            Pair("1 soat oldin", 60)
        }
        6 -> {
            Pair("2 soat oldin", 2 * 60)
        }
        7 -> {
            Pair("3 soat oldin", 3 * 60)
        }
        8 -> {
            Pair("5 soat oldin", 5 * 60)
        }
        9 -> {
            Pair("10 soat oldin", 10 * 60)
        }
        10 -> {
            Pair("1 kun oldin", 24 * 60)
        }
        11 -> {
            Pair("2 kun oldin", 2 * 24 * 60)
        }
        else -> {
            Pair("1 hafta oldin", 7 * 24 * 60)
        }
    }
}