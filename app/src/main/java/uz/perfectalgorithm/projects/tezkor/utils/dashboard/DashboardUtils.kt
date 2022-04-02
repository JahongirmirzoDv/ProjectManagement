package uz.perfectalgorithm.projects.tezkor.utils.dashboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.datepicker.MaterialDatePicker
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.Period
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.GenderEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.dashboard.*
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.ByStatusItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DashboardStatistics
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.StaffSByDepartmentItem
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toMonthName
import uz.perfectalgorithm.projects.tezkor.utils.tasks.toTwoDigit
import java.util.*

/**
 *Created by farrukh_kh on 7/17/21 1:44 PM
 *uz.rdo.projects.projectmanagement.utils.dashboard
 **/
fun Context.getTypeface(@FontRes fontId: Int = R.font.inter_regular) =
    ResourcesCompat.getFont(this, fontId)

fun getDrawable(context: Context, position: Int): Drawable {
    val drawable =
        ContextCompat.getDrawable(context, R.drawable.legend_drawable) as GradientDrawable
    drawable.setColor(getColorForPosition(context, position))
    return drawable
}

private fun getColorForPosition(context: Context, position: Int): Int {
    val defaultColors = getColors(context)

    return if (position < defaultColors.size - 1) {
        defaultColors[position]
    } else {
        defaultColors[position % defaultColors.size]
    }
}

fun getColors(context: Context): List<Int> {
    val colors = mutableListOf(
        ContextCompat.getColor(context, R.color.project_new),
        ContextCompat.getColor(context, R.color.project_in_progress),
        ContextCompat.getColor(context, R.color.project_done),
        ContextCompat.getColor(context, R.color.project_in_progress2),
        ContextCompat.getColor(context, R.color.project_done2),
        ContextCompat.getColor(context, R.color.project_new2)
    )

    listOf(
        0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
        0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
        0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
        0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
        0xff795548, 0xff9e9e9e, 0xff607d8b, 0xff333333
    ).forEach {
        colors.add(it.toInt())
    }

    return colors
}


private fun AllWorkersResponse.DataItem.getAge() = if (birthDate.isNullOrBlank()) {
    null
} else {
    Period(LocalDate.parse(birthDate), LocalDate.now()).years
}

fun List<AllWorkersResponse.DataItem>.groupByAge(): List<Pair<String, Int>> {
    var below20 = 0
    var to30 = 0
    var to40 = 0
    var to50 = 0
    var above50 = 0
    var unknown = 0

    forEach { worker ->
        val age = worker.getAge()

        if (age == null) {
            unknown++
        } else {
            when {
                age <= 20 -> below20++
                (20 < age) && (age <= 30) -> to30++
                (30 < age) && (age <= 40) -> to40++
                (40 < age) && (age <= 50) -> to50++
                age > 50 -> above50++
                else -> unknown++
            }
        }
    }

    return listOf(
        Pair("20 yoshgacha", below20),
        Pair("21-30 yosh", to30),
        Pair("31-40 yosh", to40),
        Pair("41-50 yosh", to50),
        Pair("50 yoshdan katta", above50),
        Pair("Noma'lum", unknown)
    )
}

fun DashboardStatistics.toCountData() = listOf(
    DashboardCountItem("Maqsadlar", goals.count),
    DashboardCountItem("Proyektlar", projects.count),
    DashboardCountItem("Vazifalar", tasks.count),
    DashboardCountItem("Majlislar", 0),
    DashboardCountItem("Uchrashuvlar", 0),
    DashboardCountItem("Tezkor rejalar", quickPlansCount)
)

fun DashboardStatistics.toChartData(): List<DashboardChartItem> {

    val data = mutableListOf<DashboardChartItem>()

    if (goalPercent != null) data.add(GoalChartItem(0, "Maqsad", listOf(goalPercent)))

    if (!goals.byStatus.isNullOrEmpty()) data.add(
        PieChartItem(
            1,
            "Maqsadlar",
            goals.byStatus.toByStatusEntries()
        )
    )
    if (!projects.byStatus.isNullOrEmpty()) data.add(
        PieChartItem(
            2,
            "Proyektlar",
            projects.byStatus.toByStatusEntries()
        )
    )
    if (!tasks.byStatus.isNullOrEmpty()) data.add(
        PieChartItem(
            3,
            "Vazifalar",
            tasks.byStatus.toByStatusEntries()
        )
    )
    if (!offers.byStatus.isNullOrEmpty()) data.add(
        PieChartItem(
            4,
            "Takliflar",
            offers.byStatus.toByStatusEntries()
        )
    )
    if (!complaints.byStatus.isNullOrEmpty()) data.add(
        PieChartItem(
            5,
            "Shikoyatlar",
            complaints.byStatus.toByStatusEntries()
        )
    )
    if (!allStaff.isNullOrEmpty()) {
        data.add(PieChartItem(6, "Xodimlar: Jins bo'yicha", allStaff.toByGenderEntries()))
        data.add(PieChartItem(7, "Xodimlar: Yosh bo'yicha", allStaff.toByAgeEntries()))
    }
    if (!staffsByDepartment.isNullOrEmpty()) data.add(
        BarChartItem(
            8,
            "Xodimlar: Bo'limlar bo'yicha",
            staffsByDepartment.map { it.departmentTitle },
            staffsByDepartment.toByDepartmentEntries()
        )
    )

    return data.sortedBy { it.id }
}

fun List<ByStatusItem>.toByStatusEntries() =
    if (firstOrNull()?.status == null) toByDynamicStatusEntries() else toByStaticStatusEntries()

fun List<ByStatusItem>.toByDynamicStatusEntries() =
    map { PieEntry(it.itemCount.toFloat(), it.statusTitle) }

fun List<ByStatusItem>.toByStaticStatusEntries() = map {
    PieEntry(
        it.itemCount.toFloat(),
        when (it.status) {
            "new" -> "Yangi"
            "in_progress" -> "Bajarilmoqda"
            "done" -> "Bajarilgan"
            "began" -> "Boshlangan"
            "confirmed" -> "Tasdiqlangan"
            "rejected" -> "Rad etilgan"
            "checked_by_owner" -> "Boshliq tomonidan tekshirilgan"
            "checked_by_leader" -> "Rahbar tomonidan tekshirilgan"
            "confirm_rejection" -> "Rad qabul qilingan"
            "accepted" -> "Qabul qilingan"
            else -> ""
        }
    )
}

fun List<StaffSByDepartmentItem>.toByDepartmentEntries() =
    mapIndexed { index, item -> BarEntry(index.toFloat(), item.staffsCount.toFloat()) }

fun List<AllWorkersResponse.DataItem>.toByGenderEntries() = listOf(
    PieEntry(filter { it.sex == GenderEnum.MALE.text }.size.toFloat(), "Erkak"),
    PieEntry(filter { it.sex == GenderEnum.FEMALE.text }.size.toFloat(), "Ayol"),
    PieEntry(filter { it.sex == GenderEnum.NONE.text }.size.toFloat(), "Noma'lum")
)

fun List<AllWorkersResponse.DataItem>.toByAgeEntries() = groupByAge().map { group ->
    PieEntry(group.second.toFloat(), group.first)
}

fun Fragment.showDateRangePicker(
    selections: androidx.core.util.Pair<LocalDate, LocalDate>,
    onSelected: SingleBlock<androidx.core.util.Pair<LocalDate, LocalDate>>
) {
    val builder = MaterialDatePicker.Builder.dateRangePicker().apply {
        setTheme(R.style.ThemeOverlay_App_DatePicker)
        setSelection(
            androidx.core.util.Pair(
                selections.first.toMillis(),
                selections.second.toMillis()
            )
        )
    }

    val picker = builder.build()

    picker.addOnPositiveButtonClickListener { newSelections ->
        onSelected(
            androidx.core.util.Pair(
                newSelections.first.toLocalDate(),
                newSelections.second.toLocalDate()
            )
        )
    }

    picker.show(childFragmentManager, null)
}

fun androidx.core.util.Pair<LocalDate, LocalDate>.toUiDateRange(): String {
    try {
        var text = ""

        text += first.dayOfMonth.toTwoDigit()

        if (first.monthOfYear == second.monthOfYear) {
            text += " - "
            if (first.dayOfMonth != second.dayOfMonth) {
                text += "${second.dayOfMonth.toTwoDigit()} "
            }
            text += first.monthOfYear.toMonthName()
        } else {
            text += " ${first.monthOfYear.toMonthName()}"
            text += " - "
            text += second.dayOfMonth.toTwoDigit()
            text += " ${second.monthOfYear.toMonthName()}"
        }
        return text
    } catch (e: Exception) {
        return ""
    }
}

fun LocalDate.toMillis() =
    Duration(JAN_1_1970.toDateTime(), this.toDateTimeAtStartOfDay()).millis

fun Long.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return LocalDate.fromCalendarFields(calendar)
}

private val JAN_1_1970 = LocalDateTime(1970, 1, 1, 0, 0)