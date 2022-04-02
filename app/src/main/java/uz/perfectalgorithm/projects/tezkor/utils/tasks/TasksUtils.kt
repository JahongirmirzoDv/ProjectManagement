package uz.perfectalgorithm.projects.tezkor.utils.tasks

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.tactic_plan.*

/**
 *Created by farrukh_kh on 7/22/21 11:00 AM
 *uz.rdo.projects.projectmanagement.utils.tasks
 **/
fun LocalDateTime.getDate() = "$year-${monthOfYear.toTwoDigit()}-${dayOfMonth.toTwoDigit()}"

fun LocalDateTime.getTime() = "${hourOfDay.toTwoDigit()}:${minuteOfHour.toTwoDigit()}"

fun Pair<List<TacticPlan>, List<Status>>.toStructuredList(): List<TacticPlanYear> {
    val tacticPlanStatuses = mutableListOf<TacticPlanStatus>()

    val today = LocalDate.now()
    val currentYear = today.year

    val previousYearTacticPlans = mutableListOf<TacticPlanMonth>()
    val currentYearTacticPlans = mutableListOf<TacticPlanMonth>()
    val after1YearTacticPlans = mutableListOf<TacticPlanMonth>()
    val after2YearTacticPlans = mutableListOf<TacticPlanMonth>()
    val after3YearTacticPlans = mutableListOf<TacticPlanMonth>()

    val months = listOf(
        Pair(1, "Yanvar"),
        Pair(2, "Fevral"),
        Pair(3, "Mart"),
        Pair(4, "Aprel"),
        Pair(5, "May"),
        Pair(6, "Iyun"),
        Pair(7, "Iyul"),
        Pair(8, "Avgust"),
        Pair(9, "Sentyabr"),
        Pair(10, "Oktyabr"),
        Pair(11, "Noyabr"),
        Pair(12, "Dekabr"),
    )

    second.forEach { status ->
        tacticPlanStatuses.add(
            TacticPlanStatus(
                status.id,
                status.title,
                first.filter { it.status.id == status.id })
        )
    }

    months.forEach { month ->
        val previousYearMonthStatuses = mutableListOf<TacticPlanStatus>()
        val currentYearMonthStatuses = mutableListOf<TacticPlanStatus>()
        val after1YearMonthStatuses = mutableListOf<TacticPlanStatus>()
        val after2YearMonthStatuses = mutableListOf<TacticPlanStatus>()
        val after3YearMonthStatuses = mutableListOf<TacticPlanStatus>()

        tacticPlanStatuses.forEach { tacticPlanStatus ->
            previousYearMonthStatuses.add(
                TacticPlanStatus(
                    tacticPlanStatus.id,
                    tacticPlanStatus.title,
                    tacticPlanStatus.tacticPlans.filter {
                        LocalDate.parse(it.date).year == currentYear - 1 &&
                                LocalDate.parse(it.date).monthOfYear == month.first
                    })
            )
            currentYearMonthStatuses.add(
                TacticPlanStatus(
                    tacticPlanStatus.id,
                    tacticPlanStatus.title,
                    tacticPlanStatus.tacticPlans.filter {
                        LocalDate.parse(it.date).year == currentYear &&
                                LocalDate.parse(it.date).monthOfYear == month.first
                    })
            )
            after1YearMonthStatuses.add(
                TacticPlanStatus(
                    tacticPlanStatus.id,
                    tacticPlanStatus.title,
                    tacticPlanStatus.tacticPlans.filter {
                        LocalDate.parse(it.date).year == currentYear + 1 &&
                                LocalDate.parse(it.date).monthOfYear == month.first
                    })
            )
            after2YearMonthStatuses.add(
                TacticPlanStatus(
                    tacticPlanStatus.id,
                    tacticPlanStatus.title,
                    tacticPlanStatus.tacticPlans.filter {
                        LocalDate.parse(it.date).year == currentYear + 2 &&
                                LocalDate.parse(it.date).monthOfYear == month.first
                    })
            )
            after3YearMonthStatuses.add(
                TacticPlanStatus(
                    tacticPlanStatus.id,
                    tacticPlanStatus.title,
                    tacticPlanStatus.tacticPlans.filter {
                        LocalDate.parse(it.date).year == currentYear + 3 &&
                                LocalDate.parse(it.date).monthOfYear == month.first
                    })
            )
        }

        previousYearTacticPlans.add(
            TacticPlanMonth(month.first, month.second, previousYearMonthStatuses)
        )
        currentYearTacticPlans.add(
            TacticPlanMonth(month.first, month.second, currentYearMonthStatuses)
        )
        after1YearTacticPlans.add(
            TacticPlanMonth(month.first, month.second, after1YearMonthStatuses)
        )
        after2YearTacticPlans.add(
            TacticPlanMonth(month.first, month.second, after2YearMonthStatuses)
        )
        after3YearTacticPlans.add(
            TacticPlanMonth(month.first, month.second, after3YearMonthStatuses)
        )
    }

    return listOf(
        TacticPlanYear(currentYear - 1, "${currentYear - 1}", previousYearTacticPlans),
        TacticPlanYear(currentYear, currentYear.toString(), currentYearTacticPlans),
        TacticPlanYear(currentYear + 1, "${currentYear + 1}", after1YearTacticPlans),
        TacticPlanYear(currentYear + 2, "${currentYear + 2}", after2YearTacticPlans),
        TacticPlanYear(currentYear + 3, "${currentYear + 3}", after3YearTacticPlans),
    )
}

fun String.toUiDate(): String {
    val date = LocalDateTime.parse(this)
    return "${date.dayOfMonth.toTwoDigit()}.${date.monthOfYear.toTwoDigit()}.${date.year}"
}

fun String.toMeetingDate(): String {
    val date = LocalDateTime.parse(this)
    return "${date.dayOfMonth.toTwoDigit()}.${date.monthOfYear.toTwoDigit()}.${date.year} | ${date.hourOfDay.toTwoDigit()}:${date.minuteOfHour.toTwoDigit()}"
}

fun Pair<String, String>.toUiDate(): String {
    try {
        var text = ""
        val startTime = LocalDateTime.parse(first)
        val endTime = LocalDateTime.parse(second)

        text += startTime.dayOfMonth.toTwoDigit()

        if (startTime.monthOfYear == endTime.monthOfYear) {
            text += " - "
            if (startTime.dayOfMonth != endTime.dayOfMonth) {
                text += "${endTime.dayOfMonth.toTwoDigit()} "
            }
            text += startTime.monthOfYear.toMonthName()
        } else {
            text += " ${startTime.monthOfYear.toMonthName()}"
            text += " - "
            text += endTime.dayOfMonth.toTwoDigit()
            text += " ${endTime.monthOfYear.toMonthName()}"
        }
        return text
    } catch (e: Exception) {
        return ""
    }
}

fun Int.toTwoDigit() = if (this < 10) "0$this" else "$this"

fun Int.toMonthName() = when (this) {
    1 -> "yan."
    2 -> "fev."
    3 -> "mar."
    4 -> "apr."
    5 -> "may"
    6 -> "iyun"
    7 -> "iyul"
    8 -> "avg."
    9 -> "sen."
    10 -> "okt."
    11 -> "noy."
    12 -> "dek."
    else -> ""
}