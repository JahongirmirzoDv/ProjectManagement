package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogRepeatNoteBinding
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import kotlin.math.pow

class RepeatRuleWeeklyDialog(
    fragmentContext: Context,
    private val currentRepeatRule: Int,
    val callback: (repeatRule: Int) -> Unit
) : BottomSheetDialog(fragmentContext) {
    private var _binding: DialogRepeatNoteBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")

    init {
        _binding = DialogRepeatNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    private fun loadViews() {
        val days = context.resources.getStringArray(R.array.week_days)
        binding.apply {
            for (i in 0..6) {
                val pow = 2.0.pow(i.toDouble()).toInt()
                (layoutInflater.inflate(
                    R.layout.item_repeat_week_checkbox,
                    null
                ) as AppCompatCheckBox).apply {
                    isChecked = currentRepeatRule and pow != 0
                    timberLog(isChecked.toString(), "YYYYYY")
                    timberLog(currentRepeatRule.toString(), "YYYYYY")
                    text = days[i]
                    id = pow
                    binding.verticalLayout.addView(this)
                }
            }
            backBtn.setOnClickListener {
                dismiss()
            }
            okBtn.setOnClickListener {
                callback(sumRepeat())
                dismiss()
            }
        }
    }

    private fun sumRepeat(): Int {
        var sum = 0
        val cnt = binding.verticalLayout.childCount
        for (i in 0 until cnt) {
            val child = binding.verticalLayout.getChildAt(i)
            if (child is AppCompatCheckBox) {
                if (child.isChecked)
                    sum += child.id
            }
        }
        return sum
    }
}