package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.note

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.DialogRepeatNoteBinding
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*

class RadioGroupDialog(
    private val fragmentContext: Context,
    private val startDate: DateTime,
    private val currentRepeatRule: Int,
    private val isMonth: Boolean,
    val callback: (repeatRule: Int) -> Unit
) : BottomSheetDialog(fragmentContext) {
    private var _binding: DialogRepeatNoteBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("View wasn't created")
    private var repeatRule = 0

    init {
        _binding = DialogRepeatNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadViews()
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    private fun loadViews() {
        val weekList = context.resources.getStringArray(R.array.week_days)
        val dayOfWeekTitle = weekList[startDate.dayOfWeek - 1]
        val day = startDate.dayOfMonth
        val order = (day - 1) / 7 + 1
        val monthOfYear = Formatter.getMonthName(fragmentContext, startDate.monthOfYear)
        val radioGroup = RadioGroup(fragmentContext)

        (layoutInflater.inflate(
            R.layout.item_repeat_week_radiobutton,
            null
        ) as AppCompatRadioButton).apply {
            text = if (isMonth) {
                "Har oyning $day-sanasida"
            } else {
                "Har $monthOfYear ning $day-sanasida"
            }
            radioGroup.addView(this)
            id = REPEAT_SAME_DAY
        }

        (layoutInflater.inflate(
            R.layout.item_repeat_week_radiobutton,
            null
        ) as AppCompatRadioButton).apply {
            text = if (isMonth) {
                "Har oyning ${order}-${dayOfWeekTitle}sida"
            } else {
                "Har ${monthOfYear}ning ${order}-${dayOfWeekTitle}sida"
            }
            radioGroup.addView(this)
            id = REPEAT_ORDER_WEEKDAY
        }


        if (startDate.dayOfMonth == startDate.dayOfMonth().maximumValue) {
            (layoutInflater.inflate(
                R.layout.item_repeat_week_radiobutton,
                null
            ) as AppCompatRadioButton).apply {
                text = if (isMonth) {
                    "Har oyning oxirgi sanasida"
                } else {
                    "Har ${monthOfYear}ning oxirgi sanasida"
                }
                radioGroup.addView(this)
                id = REPEAT_LAST_DAY
            }
        }
        if (startDate.monthOfYear != startDate.plusDays(7).monthOfYear) {
            (layoutInflater.inflate(
                R.layout.item_repeat_week_radiobutton,
                null
            ) as AppCompatRadioButton).apply {
                if (isMonth) {
                    text = "Har oyning oxirgi ${dayOfWeekTitle}sida"
                } else {
                    text = "Har ${monthOfYear}ning oxirgi ${dayOfWeekTitle}sida"
                }
                radioGroup.addView(this)
                id = REPEAT_ORDER_WEEKDAY_USE_LAST
            }
        }

        radioGroup.check(currentRepeatRule)


        binding.verticalLayout.addView(radioGroup)
        binding.apply {

            backBtn.setOnClickListener {
                dismiss()
            }
            okBtn.setOnClickListener {
                repeatRule = radioGroup.checkedRadioButtonId
                callback(repeatRule)
                dismiss()
            }
        }
    }

}

