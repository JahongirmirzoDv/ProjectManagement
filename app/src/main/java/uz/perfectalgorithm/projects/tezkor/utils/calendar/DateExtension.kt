package uz.perfectalgorithm.projects.tezkor.utils.calendar

import org.joda.time.DateTime
import kotlin.math.pow

fun DateTime.seconds() = millis / 1000L


fun Int.isTsOnProperDay(repeatRule: Int): Boolean {
    val power = 2.0.pow((this - 1).toDouble()).toInt()
    return repeatRule and power != 0
}