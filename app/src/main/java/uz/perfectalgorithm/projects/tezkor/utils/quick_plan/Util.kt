package uz.perfectalgorithm.projects.tezkor.utils.quick_plan

import android.util.Log
import org.joda.time.LocalDate
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlan
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.quick_plan.QuickPlanDay
import uz.perfectalgorithm.projects.tezkor.utils.adding.toUiDate

/**
 *Created by farrukh_kh on 8/18/21 3:25 PM
 *uz.rdo.projects.projectmanagement.utils.quick_plan
 **/
fun List<QuickPlan>.toWeekList(date: LocalDate): List<QuickPlanDay> {
    val daysList = mutableListOf<QuickPlanDay>()
    var day = date.withDayOfWeek(1)

    for (i in 0..6) {
        daysList.add(
            QuickPlanDay(
                day.dayOfWeek.toWeekDay(),
                day.toUzDate(),
                filter { it.date == day.toUiDate() })
        )
        day = day.plusDays(1)
    }

    return daysList
}

fun List<QuickPlan>.toMonthList(date: LocalDate): List<QuickPlanDay> {
    val daysList = mutableListOf<QuickPlanDay>()
    var day = date.withDayOfMonth(1)

    do {
        daysList.add(
            QuickPlanDay(
                day.dayOfWeek.toWeekDay(),
                day.toUzDate(),
                filter { it.date == day.toUiDate() })
        )
        day = day.plusDays(1)
    } while (day.monthOfYear == date.monthOfYear)

//    while (true) {
//        if (day.monthOfYear < date.monthOfYear || day.monthOfYear > date.monthOfYear) break
//        daysList.add(
//            QuickPlanDay(
//                day.dayOfWeek.toWeekDay(),
//                day.toUzDate(),
//                filter { it.date == day.toUiDate() })
//        )
//        day = day.plusDays(1)
//    }

    return daysList
}

fun Int.toWeekDay() = when (this) {
    1 -> "Dushanba"
    2 -> "Seshanba"
    3 -> "Chorshanba"
    4 -> "Payshanba"
    5 -> "Juma"
    6 -> "Shanba"
    7 -> "Yakshanba"
    else -> throw IllegalArgumentException("from 1 to 7 only")
}

fun LocalDate.toUzDate() = "${dayOfMonth}-${monthOfYear.getMonthName()}, $year"

fun Int.getMonthName() = when (this) {
    1 -> "Yanvar"
    2 -> "Fevral"
    3 -> "Mart"
    4 -> "Aprel"
    5 -> "May"
    6 -> "Iyun"
    7 -> "Iyul"
    8 -> "Avgust"
    9 -> "Sentyabr"
    10 -> "Oktyabr"
    11 -> "Noyabr"
    12 -> "Dekabr"
    else -> throw Exception("There are only 12 months")
}